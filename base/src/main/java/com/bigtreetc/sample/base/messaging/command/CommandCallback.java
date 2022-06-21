package com.bigtreetc.sample.base.messaging.command;

import com.bigtreetc.sample.base.messaging.event.EventResult;

public interface CommandCallback {

  void callback(EventResult result);
}
