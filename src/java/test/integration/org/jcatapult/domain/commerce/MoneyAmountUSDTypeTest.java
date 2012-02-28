/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
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
package org.jcatapult.domain.commerce;

import java.math.BigDecimal;
import java.util.List;

import org.jcatapult.persistence.service.jpa.PersistenceService;
import org.jcatapult.persistence.test.BaseJPATest;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import static org.testng.Assert.*;

/**
 * This class tests the hibernate types.
 *
 * @author Brian Pontarelli
 */
public class MoneyAmountUSDTypeTest extends BaseJPATest {
  @Inject public PersistenceService persistenceService;

  @Test
  public void save() {
    MoneyAmountHolder holder = new MoneyAmountHolder();
    holder.setMoney(Money.of(CurrencyUnit.getInstance("USD"), 1.99));
    persistenceService.persist(holder);

    List<MoneyAmountHolder> holders = persistenceService.findAllByType(MoneyAmountHolder.class);
    assertEquals(holders.size(), 1);
    assertEquals(holders.get(0).getMoney().getAmount(), new BigDecimal("1.99"));
    assertEquals(holders.get(0).getMoney().getCurrencyUnit(), CurrencyUnit.getInstance("USD"));
  }

  @Test
  public void badCurrency() {
    MoneyAmountHolder holder = new MoneyAmountHolder();
    holder.setMoney(Money.of(CurrencyUnit.getInstance("EUR"), 1.99));
    try {
      persistenceService.persist(holder);
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }
  }
}
