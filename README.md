# simple-meili
A simple, fluent Meilisearch Java client

[![coverage](https://raw.githubusercontent.com/raissi/simple-meili/main/badges/jacoco.svg)](https://github.com/raissi/simple-meili/actions/workflows/maven.yml) [![branches coverage](https://raw.githubusercontent.com/raissi/simple-meili/main/badges/branches.svg)](https://github.com/raissi/simple-meili/actions/workflows/maven.yml)


> **Meilisearch**: An open-source, lightning-fast, 
> and hyper-relevant search engine that fits effortlessly 
> into your apps, websites, and workflow.
>
> <cite>[meilisearch.com](https://www.meilisearch.com/)</cite>

And this is an effort to create a simple fluent Java client. 
This is NOT a replacement of the official Java client. 
It's just another approach to communicate with Meilisearch.  

Especially, when you communicate with Meilisearch (or any other external system), that communication may fail due to multiple causes.  
One way to enforce handling failures is to go through checked exceptions, which is not what every Java developer likes.  
Another way, adopted here, is to use a _Try_ monad. It's returned by each Meilisearch request.  
We are defining our own _Try_ implementation, 
but it was hugely inspired from the [Vavr library](https://www.vavr.io/), and also the one defined inside [Junit 5](https://junit.org/junit5/docs/5.4.0/api/org/junit/platform/commons/function/Try.html).  

### Maven artifact
```xml
<dependency>
    <groupId>io.github.raissi</groupId>
    <artifactId>simple-meili-java</artifactId>
    <version>0.29.1</version>
</dependency>
```
Development of this client started with version `0.29.1` of Meilsearch. Older versions are not supported.

### HTTP Client
For now, only [OkHttp](https://square.github.io/okhttp/) is supported:
```java
OkHttpClient okHttpClient = new OkHttpClient();
MeiliClient client = MeiliClientOkHttp.usingOkHttp(okHttpClient)
        .forHost("http://localhost:7700")
        .withSearchKey("masterKey");
```

### JSON ser/de
By default the Jackson library is used to read/write Json. And so you need to add it to your dependencies:

```xml
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-core</artifactId>
      <version>${jackson.version}</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>${jackson.version}</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.datatype</groupId>
      <artifactId>jackson-datatype-jsr310</artifactId>
      <version>${jackson.version}</version>
    </dependency>
```
But you can use other libraries by defining your own `JsonWriter` and `JsonReader` implementations (more on that later).

### Usage
A complete example is available here [EndToEndTest.java](https://github.com/raissi/simple-meili/blob/main/src/test/java/io/github/meilisearch/integration/EndToEndITest.java)  
In that example, to insert a collection of documents, we start by preparing the request:
```java
UpsertDocuments upsert = MeiliQueryBuilder.intoIndex("nameOfTheIndex")
        .upsertDocuments(theListToInsert)
        .withPrimaryKey("nameOfThePrimaryKeyField");
```
After that we make the call:
```java
Try<CanBlockOnTask> callResult = client.upsert(upsert);
```
The call can fail for many reasons (network, server unavailable, etc...), that's why the `upsert` call returns
a `Try` object. It's called a monad, and can have one of two states: either it is the result of a successful operation
and so contains an object of referenced type (in this case a `CanBlockOnTask` instance, more on that in a moment);
or it's the result of a failure to execute and so contains the Exception related to that failure.  
As for the `CanBlockOnTask` class, it's simply a class of objects that can block for a call to complete. 
Since writing queries in Meilisearch are async, this class offers a utility method `waitForCompletion` that initiates a call 
to server each 20ms (default implementation) to check if requested operation completed. Here is how to invoke it:
```java
Try<MeiliTask> task = client.upsert(upsert)
       .andThen(CanBlockOnTask::waitForCompletion);
```
You can access the encapsulated task in many ways: `.orElseThrow(Function.identity());` or `.ignoreErrors();`
which returns an `Optional` containing the task if the original call was successful, ignoring eventual errors (hence the name)