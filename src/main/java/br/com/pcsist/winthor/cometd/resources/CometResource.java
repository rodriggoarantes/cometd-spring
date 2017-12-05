package br.com.pcsist.winthor.cometd.resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/rest")
public class CometResource {
  
  @RequestMapping(value = "/mensagem", method = RequestMethod.GET)
  public String mensagem() {
    return "teste mensagem resource";
  }
}
