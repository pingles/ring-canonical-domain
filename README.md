# ring-canonical-domain

Helps generate 301 permanent redirects for handling canonical domains. For example, when you want anyone accessing http://domain.com to be redirected to http://www.domain.com.

## Usage

Add to your `project.clj` as follows:

    [ring-canonical-domain "0.0.1"]

Then, wrap your app as follows:

```clojure
(use 'ring-canonical-domain.middleware)
(use 'ring.adapter.jetty)

(defn handler [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello, world"})

(def matcher (partial server-name-matches? #"^example\.com"))

(def app (-> handler
             (wrap-canonical-redirect matcher (redirect-host "www.example.com")))

(run-jetty app {:port 8080})
```clojure

## License

Copyright &copy; 2012 Paul Ingles

Distributed under the Eclipse Public License, the same as Clojure.
