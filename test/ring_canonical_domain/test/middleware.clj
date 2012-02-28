(ns ring-canonical-domain.test.middleware
  (:use [clojure.test]
        [ring-canonical-domain.middleware] :reload))

(defn simple-app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Test App"})

(defn request [m]
  (merge {:remote-addr "0:0:0:0:0:0:0:1%0"
          :scheme :http
          :request-method :get
          :query-string nil
          :content-type nil
          :uri "/"
          :server-name "localhost"
          :headers {:host "localhost:8080"}
          :server-port 8080}
         m))

(deftest undecorated-app
  (is (= 200 (:status (simple-app (request {}))))))

(deftest decorated-with-constant-url
  (let [req (request {})]
    (is (= 200
           (:status ((wrap-canonical-redirect simple-app
                                              (constantly false)
                                              (constantly "url"))
                     req))))
    (let [resp ((wrap-canonical-redirect simple-app
                                         (constantly true)
                                         (constantly "url"))
                req)]
      (is (= 301 (:status resp)))
      (is (= {"Location" "url"} (:headers resp))))))

(deftest decorated-with-rewritten-url
  (let [resp ((wrap-canonical-redirect simple-app
                                       (constantly true)
                                       (redirect-host "www.domain.com"))
              (request {:uri "/some-uri"}))]
    (is (= "http://www.domain.com/some-uri"
           (get-in resp [:headers "Location"])))))

(deftest detects-hosts-to-redirect
  (let [matcher (partial server-name-matches? #"^example\.com")]
    (is (true? (matcher {:server-name "example.com"})))
    (is (false? (matcher {:server-name "www.example.com"})))))