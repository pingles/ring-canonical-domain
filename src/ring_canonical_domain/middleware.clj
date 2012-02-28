(ns ring-canonical-domain.middleware)

(defn permanent-redirect
  [url]
  {:status 301
   :headers {"Location" url}
   :body ""})

(defn redirect-host
  "Returns a function that, when called with a rack request
   map, returns a URL with the request's scheme and URI but
   updated host."
  [host]
  (fn [req]
    (str (name (:scheme req)) "://" host (:uri req))))

(defn server-name-matches?
  "A predicate to help matching requests by server-name.
   For example, to redirect non-www requests, match to any
   example.com server-name that doesn't start www:

   (partial server-name-matches #\"^example.com\")"
  [re req]
  (not (nil? (re-matches re (:server-name req)))))

(defn wrap-canonical-redirect
  "Decorates an application to issue a canonical redirect
   if the request is for a non-canonical host, for example,
   when using both example.com and www.example.com.

   (wrap-canonical-redirect app blah (constantly \"http://domain.com/\""
  [app should-redirect? url]
  (fn [req]
    (if (should-redirect? req)
      (permanent-redirect (url req))
      (app req))))