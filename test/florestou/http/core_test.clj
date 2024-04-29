(ns florestou.http.core-test
  {:clj-kondo/config '{:linters {:unresolved-symbol {:exclude [system]}}}}
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [florestou.containers.postgres :refer [pg-fixture]]
            [florestou.test-helpers :refer [with-system test-system-map]]
            [clj-http.client :as http-client]))

(use-fixtures :once pg-fixture)

(deftest healthcheck-test
  (with-system [system (test-system-map)]
    (testing "healthcheck api works"
      (let [response (http-client/get "http://localhost:3001/health")]
        (is (= 200 (:status response)))
        (is (= "{\"message\":\"Healthy\"}" (:body response)))))))
