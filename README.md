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


