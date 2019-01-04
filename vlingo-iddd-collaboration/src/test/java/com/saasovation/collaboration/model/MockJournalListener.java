// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package com.saasovation.collaboration.model;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.journal.JournalListener;

public class MockJournalListener implements JournalListener<String> {
  public final List<Entry<String>> allEntries = new ArrayList<>();
  public final List<State<String>> allSnapshots = new ArrayList<>();
  public TestUntil until = TestUntil.happenings(0);

  @Override
  public void appended(final Entry<String> entry) {
    synchronized (allEntries) {
    allEntries.add(entry);
    allSnapshots.add(State.TextState.Null);
    until.happened();
    }
  }

  @Override
  public void appendedWith(final Entry<String> entry, final State<String> snapshot) {
    synchronized (allEntries) {
    allEntries.add(entry);
    allSnapshots.add(snapshot);
    until.happened();
    }
  }

  @Override
  public void appendedAll(final List<Entry<String>> entries) {
    synchronized (allEntries) {
    allEntries.addAll(entries);
    for (int idx = 0; idx < entries.size(); ++idx) {
      allSnapshots.add(State.TextState.Null);
    }
    until.happened();
    }
  }

  @Override
  public synchronized void appendedAllWith(final List<Entry<String>> entries, final State<String> snapshot) {
    synchronized (allEntries) {
    allEntries.addAll(entries);
    for (int idx = 0; idx < (entries.size() - 1); ++idx) {
      allSnapshots.add(State.TextState.Null);
    }
    allSnapshots.add(snapshot == null ? State.TextState.Null : snapshot);
    until.happened();
    }
  }

  public int confirmExpectedEntries(final int count, final int retries) {
    for (int idx = 0; idx < retries; ++idx) {
      if (allEntries.size() == count) {
        return count;
      }
      try { Thread.sleep(100); } catch (Exception e) { }
    }
    return allEntries.size();
  }
}