This is a project to get started with java connection pooling.
It has the connection pooling implementation for HTTP connections.

It maintains a connection pool for github and serves the connections as per the demand.
Github URL used: https://api.github.com/users/rneha725

This demand comes from the thread pool which is setup. If connections are not available in the connection pool, then
a thread waits till a connection is freed up. This is done using `wait()` and `notify()`. `wait()` is called indefinitely
till a connection is added back to the pool. A thread gets out of `wait()` when a `notify()` is called from another thread.