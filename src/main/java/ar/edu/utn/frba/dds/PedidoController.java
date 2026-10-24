package ar.edu.utn.frba.dds;

import static org.springframework.http.ResponseEntity.ok;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

  public PedidoController() {

  }

  @GetMapping("/{id}")
  public ResponseEntity<String> agregarItem(
      @PathVariable String id) {
    return ok("OK");
  }
  @PostMapping("/{id}/items")
  public ResponseEntity<String> agregarItem(
      @PathVariable String id,
      @RequestBody String echo) {
    return ok(echo);
  }
}