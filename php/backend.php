<?php
//Set the php process never to expire..
set_time_limit(0);
date_default_timezone_set("Asia/Hong_Kong");
header("Content-Type: text/event-stream\n\n");

while (1) {
  // Every second, sent the server time to the browser
  echo "event: message\n";
  $current_time = date("H:i:s");
  echo 'data: ' . $current_time . '"';
  echo "\n\n";
  ob_flush();
  flush();
  sleep(1);
}