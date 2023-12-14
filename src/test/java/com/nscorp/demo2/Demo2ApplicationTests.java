package com.nscorp.demo2;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

//import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class Demo2ApplicationTests {

  @Autowired
  private WebTestClient client;

  @Test
  public void hahaTest() throws Exception {
    this.client.get().uri("/greeting/dwhaha").exchange().expectStatus().isOk();
  }

  @Test
  public void dwTest() throws Exception {
    this.client.get().uri("/dw").exchange().expectStatus().isOk();
  }

  @Test
  public void cveTest() throws Exception {
//    ResponseSpec spec = this.client.get().uri("/cve/5dbeeb13337d0e08c669f5d2c6ff8a5a6313e1ed85f4002bc4fe75f9112d3bd4").exchange();
    ResponseSpec spec = this.client.get().uri("/cve/test").exchange();
    System.out.println(spec.toString());
    Assert.assertNotNull(spec);
  }

  @Test
  public void anchoreTest() throws Exception {
	System.out.println("anchor_cve test");
	ResponseSpec spec = this.client.get().uri("/anchore/aaae47b6ecb4347ae909b71a459d10f26621cc5cd580aec2745d42e76bbd0d9b?tag='nsos-pr1-nex.atldc.nscorp.com:8082/mobile/dwtest:latest'").exchange();
	System.out.println(spec.expectBody().returnResult());
	
	Assert.assertNotNull(spec);
  }
    
}
