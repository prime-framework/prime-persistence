/*
 * Copyright (c) 2001-2010, Inversoft Inc., All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.primeframework.persistence.txn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the default implementation of the transactional context.
 *
 * @author Brian Pontarelli
 */
public class DefaultTransactionContext implements TransactionContext {
  private final static Logger logger = LoggerFactory.getLogger(DefaultTransactionContext.class);
  private final List<TransactionalResource> resources = new ArrayList<TransactionalResource>();
  private boolean started;
  private boolean committed;
  private boolean rolledBack;
  private boolean rollbackOnly;

  /**
   * {@inheritDoc}
   */
  @Override
  public void start() {
    if (started) {
      throw new TransactionException("The transaction has already been started. You can only start the transaction once");
    }

    if (resources.size() > 0) {
      try {
        for (TransactionalResource resource : resources) {
          resource.start();
        }
      } catch (Exception e) {
        // Failure state. Attempt to rollback any started transactions and throw an exception
        rolledBack = true;
        for (TransactionalResource resource : resources) {
          try {
            resource.rollback();
          } catch (Exception e2) {
            logger.error("Unable to rollback a transaction after one of the transactions being managed was unable to be started.", e2);
          }
        }

        throw new TransactionException("Error while starting transactions", e);
      }
    }

    started = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isStarted() {
    return started;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void commit() throws Exception {
    if (!started) {
      throw new TransactionException("The transaction has not been started. You must start the transaction before " +
        "you can commit it.");
    }

    if (committed) {
      return;
    }

    if (rolledBack) {
      throw new TransactionException("The transaction has already been rolled back and can't be committed.");
    }

    if (rollbackOnly) {
      throw new TransactionException("The transaction can't be committed because it has been set as rollbackOnly.");
    }

    int index = 0;
    for (Iterator<TransactionalResource> i = resources.iterator(); i.hasNext(); ) {
      TransactionalResource resource = i.next();
      i.remove();
      try {
        resource.commit();
      } catch (Exception e) {
        if (index != 0) {
          // This is seriously bad mojo right here. We don't have two-phase commits, there are multiple
          // resources and the first couple were successful. We'll have to continue committing
          logger.error("A resource failed during commit and there aren't two phase commits available. Since this resource " +
            "was not the first resource, that means another resource completed successfully during the commit and can no " +
            "longer be rolled back. This is a very complex edge case and Prime currently doesn't support this case. " +
            "Therefore, we completed the commit and are simply logging the failure.", e);
        } else {
          try {
            rollback(); // Rollback the rest of the resources
          } catch (TransactionException te) {
            // We can ignore this I guess because we should throw the original exception
            logger.error("During the commit of the transaction, the first resource failed. During the rollback of the " +
              "remaining resources an error was encountered that was unexpected and should not have occurred. The original " +
              "exception is being thrown and this is the unexpected exception.", e);
          }

          throw e;
        }
      }

      index++;
    }

    committed = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void rollback() throws Exception {
    if (!started) {
      throw new TransactionException("The transaction has not been started. You must start the transaction before " +
        "you can roll it back.");
    }

    if (rolledBack) {
      return;
    }

    if (committed) {
      throw new TransactionException("The transaction has already been committed and can't be rolled back.");
    }

    boolean failed = false;
    for (TransactionalResource resource : resources) {
      try {
        resource.rollback();
      } catch (Exception e) {
        failed = true;
        logger.error("A resource failed to be rolled back during a rollback of the current transaction. This rollback " +
          "occurred because the " +
          (rollbackOnly ? "transaction was set to rollback only" : "the transaction was rollback due to a failure"),
          e);
      }
    }

    // Always set the roll back flag even if they all fail
    rolledBack = true;

    if (failed) {
      throw new TransactionException("Unable to rollback all of the transactions that were active. One or more " +
        "transactions failed during rollback.");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRollbackOnly() {
    if (!started) {
      throw new TransactionException("The transaction has not been started. You must start the transaction before " +
        "you can check the rollbackOnly status.");
    }

    if (committed || rolledBack) {
      throw new TransactionException("The transaction has been committed or rolled back. The transaction must be " +
        "active in order to check the rollbackOnly status.");
    }

    return rollbackOnly;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setRollbackOnly() {
    if (!started) {
      throw new TransactionException("The transaction has not been started. You must start the transaction before " +
        "you can set the rollbackOnly status.");
    }

    if (committed || rolledBack) {
      throw new TransactionException("The transaction has been committed or rolled back. The transaction must be " +
        "active in order to set the rollbackOnly status.");
    }

    rollbackOnly = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void add(TransactionalResource resource) throws Exception {
    if (committed || rolledBack) {
      throw new TransactionException("The transaction has been committed or rolled back. You can't add new resources " +
        "once it has been completed.");
    }

    resources.add(resource);

    if (started) {
      resource.start();
    }
  }
}
