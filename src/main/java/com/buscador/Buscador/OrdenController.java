package com.buscador.Buscador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrdenController {

    @Autowired
    private ElasticSearchService elasticSearchService;


    @GetMapping("/todasLasOrdenes")
    public ResponseEntity<List<Orden>> getTodasLasOrdenes() {
        List<Orden> productos = elasticSearchService.listaDeTodasLasOrdenes();
        if (productos != null) {
            return ResponseEntity.ok(productos);
        } else {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/orden")
    public ResponseEntity<String> createOrden(@RequestBody Orden Orden) {
        String id = elasticSearchService.crearOrden(Orden);
        if (id != null) {
            return ResponseEntity.ok(id);
        } else {
            return ResponseEntity.status(500).body("Error creando orden");
        }
    }

    @DeleteMapping("/eliminarOrden")
    public ResponseEntity<String> createOrden(@RequestParam String id) {
        if (elasticSearchService.eliminarOrden(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(500).body("Error eliminando orden");
        }
    }
}