package com.nscorp.demo2;

import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
public class GreetingController {

  private static final String template = "Hey, %s here is your temp password %s";
  private final AtomicLong counter = new AtomicLong();

  @RequestMapping("/greeting/{name}")
  public Greeting greeting(@PathVariable String name) {
    String password = "bogus";
    return new Greeting(counter.incrementAndGet(), String.format(template, name, password));
  }

  @RequestMapping("/cve/{layer}")
  public ResponseEntity<String> cve(@PathVariable String layer) {
    Calendar calStart = Calendar.getInstance();
    String sb = new CVE(layer).parse();
    System.out.println(Calendar.getInstance().getTimeInMillis() - calStart.getTimeInMillis());
    return new ResponseEntity<String>(sb, HttpStatus.OK);

  }

  @RequestMapping("/anchore/{digest}")
  public ResponseEntity<String> anchore(@PathVariable String digest, @RequestParam("tag") String name) {
	System.out.println(digest + " --> " + name);
    Calendar calStart = Calendar.getInstance();
    String sb = new Anchore_CVE(digest, name).parse();
    System.out.println("response time = " + (Calendar.getInstance().getTimeInMillis() - calStart.getTimeInMillis()) + " ms");
    return new ResponseEntity<String>(sb, HttpStatus.OK);

  }

  @RequestMapping(value = "/dw")
  public ResponseEntity<String> dw(@RequestHeader Map<String, String> headers, HttpServletRequest req) {
    Calendar calStart = Calendar.getInstance();
    StringBuffer sb = new StringBuffer();
    headers.forEach((key, value) -> {
      sb.append(String.format("Header %s = %s</br>", key, value));
    });

    sb.append(String.format("%s = %s</br>", "remote addr</br>", req.getRemoteAddr()));
    sb.append(String.format("%s = %s</br>", "local  addr</br>", req.getLocalAddr()));
    sb.append(String.format("%s = %s</br>", "remote host</br>", req.getRemoteHost()));
    System.out.println(Calendar.getInstance().getTimeInMillis() - calStart.getTimeInMillis());
    return new ResponseEntity<String>(sb.toString(), HttpStatus.OK);
  }
}

