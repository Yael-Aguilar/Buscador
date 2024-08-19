package com.buscador.Buscador;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ElasticSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper objectMapper;

    public List<String> listAllIndexes() {
        GetIndexRequest request = new GetIndexRequest("*");
        try {
            GetIndexResponse getIndexResponse = client.indices().get(request, RequestOptions.DEFAULT);
            return Arrays.asList(getIndexResponse.getIndices());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createProduct(Producto producto) {
        Map<String, Object> dataMap = objectMapper.convertValue(producto, Map.class);
        IndexRequest indexRequest = new IndexRequest("producto_index").source(dataMap);
        try {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            return indexResponse.getId();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Producto> listaDeTodosLosProductos() {
        SearchRequest searchRequest = new SearchRequest("producto_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            List<Producto> productos = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                Producto producto = objectMapper.readValue(hit.getSourceAsString(), Producto.class);
                productos.add(producto);
            }
            return productos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String updateProductByNombre(Producto updatedProducto) {
        SearchRequest searchRequest = new SearchRequest("producto_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("nombre", updatedProducto.getNombre()));
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.getHits().getTotalHits().value > 0) {
                SearchHit hit = searchResponse.getHits().getHits()[0];
                String documentId = hit.getId();

                Map<String, Object> dataMap = objectMapper.convertValue(updatedProducto, Map.class);
                UpdateRequest updateRequest = new UpdateRequest("producto_index", documentId).doc(dataMap);
                UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
                return updateResponse.getId();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Producto> buscarProductosPorKeyword(String text) {
        SearchRequest searchRequest = new SearchRequest("producto_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.queryStringQuery("*" + text + "*")
                .field("nombre")
                .field("manufacturador")
                .field("categoria")
                .field("descripcionCorta")
                .field("descripcionLarga"));
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            List<Producto> productos = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                Producto producto = objectMapper.readValue(hit.getSourceAsString(), Producto.class);
                productos.add(producto);
            }
            return productos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Orden> listaDeTodasLasOrdenes() {
        SearchRequest searchRequest = new SearchRequest("orden_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            List<Orden> ordenes = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                Orden orden = objectMapper.readValue(hit.getSourceAsString(), Orden.class);
                ordenes.add(orden);
            }
            return ordenes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String crearOrden(Orden orden) {
        Map<String, Object> dataMap = objectMapper.convertValue(orden, Map.class);
        IndexRequest indexRequest = new IndexRequest("orden_index").source(dataMap);
        try {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            return indexResponse.getId();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean eliminarOrden(String id) {
        DeleteByQueryRequest request = new DeleteByQueryRequest("orden_index");
        request.setQuery(QueryBuilders.matchQuery("id", id));

        try {
            BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);
            long deleted = response.getDeleted();
            return deleted > 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}