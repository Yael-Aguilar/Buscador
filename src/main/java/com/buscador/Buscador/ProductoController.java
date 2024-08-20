package com.buscador.Buscador;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ProductoController {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @GetMapping("/indexes")
    public List<String> getAllIndexes() {
        return elasticSearchService.listAllIndexes();
    }

    @PostMapping("/producto")
    public ResponseEntity<String> createProduct(@RequestBody Producto producto) {
        String id = elasticSearchService.createProduct(producto);
        if (id != null) {
            return ResponseEntity.ok(id);
        } else {
            return ResponseEntity.status(500).body("Error creando producto");
        }
    }

    @GetMapping("/todosLosProductos")
    public ResponseEntity<List<Producto>> listAllProducts() {
        log.info("reached this");
        List<Producto> productos = elasticSearchService.listaDeTodosLosProductos();
        if (productos != null) {
            return ResponseEntity.ok(productos);
        } else {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/modificarProducto")
    public ResponseEntity<String> updateProductByNombre(@RequestBody Producto updatedProducto) {
        String id = elasticSearchService.updateProductByNombre(updatedProducto);
        if (id != null) {
            return ResponseEntity.ok("Se actualizo el producto: " + updatedProducto.getNombre());
        } else {
            return ResponseEntity.status(404).body("Product no encontrado");
        }
    }

    @GetMapping("/busquedaDeProducto")
    public ResponseEntity<List<Producto>> buscarProductosPorKeyword(@RequestParam String text) {
        List<Producto> productos = elasticSearchService.buscarProductosPorKeyword(text);
        if (productos != null) {
            return ResponseEntity.ok(productos);
        } else {
            return ResponseEntity.status(500).body(null);
        }
    }
}