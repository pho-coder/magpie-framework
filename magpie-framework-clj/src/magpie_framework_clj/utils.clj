(ns magpie-framework-clj.utils
  (:import [org.apache.zookeeper KeeperException$NodeExistsException KeeperException$NoNodeException])
  (:require [taoensso.timbre :as timbre]
            [clj-zookeeper.zookeeper :as zk]
            [com.jd.bdp.magpie.util.utils :as magpie-utils]))

(defn zk-new-client
  [zk-str]
  (zk/new-client zk-str))

(defn create-heartbeat-node
  [task-heartbeat-node]
  (try
    (zk/create task-heartbeat-node :mode :ephemeral)
    true
    (catch Exception e
      (if (= (.getClass e) KeeperException$NodeExistsException)
        false
        (do (timbre/error e)
            (throw e))))))

(defn get-task-status
  [status]
  (case status
    :reloaded "reloaded"
    :killed "killed"
    :running "running"
    :paused "paused"
    (throw (RuntimeException. (str "task status error! " status)))))

(defn set-task-status
  [task-status-node status]
  (try
    (zk/set-data task-status-node (magpie-utils/string->bytes status))
    (catch Exception e
      (if (= (.getClass e) KeeperException$NoNodeException)
        (zk/create task-status-node :mode :persistent)
        (do (timbre/error e)
            (throw e))))))

(defn get-task-command
  [command]
  (case command
    :initial "initial"
    :init "init"
    :run "run"
    :reload "reload"
    :pause "pause"
    :wait "wait"
    :kill "kill"
    (throw (RuntimeException. (str "task command error! " command)))))
