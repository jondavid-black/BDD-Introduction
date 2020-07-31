# BDD-Introduction
This repo accompanies a [presentation on Test Driven](https://github.com/jondavid-black/DevOpsForDefense/blob/master/Meetup/2020/2020-XX%20DO4D%20-%20TDD_BDD.pdf) / Behavior Driven development.  The intent is to introduce the subjects and then provide a demonstration.

The approach used here is to specificly avoid developing the application.  I use a open source web service demo (written in Go for good measure) and only
focus on the black-box acceptance testing using Cucumber.  I want to clearly demonstrate black box testing by writing my tests in Java for a Go implementation.
The test is only able to interact with the "system" through the API.

The service being tested is from [Brunoga](https://github.com/brunoga/go-webservice-sample).
