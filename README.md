# Java async

## Promise

Code, passed into Promise constructor will be executed in another thread. As soon, as result will be calculated, then chain will be triggered.

```java
    new Promise<String>(() -> {
            Thread.sleep(1000);
            return "Promise result";
    }).then((data) -> {
            out.println(data);
            Thread.sleep(1000);
            return "New promise string";
    }).then((data) -> {
        out.println(data);
    });
```

Output will be:
```
Promise result
New promise string
```