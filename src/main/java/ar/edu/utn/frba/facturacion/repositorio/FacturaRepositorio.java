package ar.edu.utn.frba.facturacion.repositorio;

import ar.edu.utn.frba.facturacion.modelo.Factura;
import ar.edu.utn.frba.facturacion.modelo.FacturaA;
import ar.edu.utn.frba.facturacion.modelo.FacturaB;
import ar.edu.utn.frba.facturacion.modelo.FacturaC;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria de facturas.
 *
 * Version LEGACY: recorre las colecciones con bucles {@code for} y
 * devuelve {@code null} cuando no encuentra un resultado.
 */
@Repository
public class FacturaRepositorio {

    private final List<Factura> facturas = new ArrayList<>();

    public FacturaRepositorio() {
        // Datos de ejemplo (se cargan al iniciar la aplicacion).
        facturas.add(new FacturaA("A-0001", 10000.0, "30-71234567-8"));
        facturas.add(new FacturaA("A-0002", 25000.0, "30-59876543-2"));
        facturas.add(new FacturaB("B-0001", 5000.0, "Juan Perez"));
        facturas.add(new FacturaB("B-0002", 8000.0, "Ana Gomez"));
        facturas.add(new FacturaC("C-0001", 3000.0));
    }

    /**
     * Devuelve todas las facturas.
     *
     * TODO PASO 4 (Streams): este metodo copia la lista con un for.
     *   Se puede resolver con Streams:
     *       return facturas.stream().toList();
     */
    public List<Factura> buscarTodas() {
        List<Factura> resultado = new ArrayList<>();
        for (Factura f : facturas) {
            resultado.add(f);
        }
        return resultado;
    }

    /**
     * Busca una factura por numero.
     *
     * TODO PASO 5 (Optional): hoy devuelve null si no existe, lo que
     *   obliga a quien la usa a acordarse de chequear el null.
     *   Migrar la firma a Optional<Factura> y resolver con Streams:
     *
     *       public Optional<Factura> buscarPorNumero(String numero) {
     *           return facturas.stream()
     *                   .filter(f -> f.getNumero().equals(numero))
     *                   .findFirst();
     *       }
     */
    public Factura buscarPorNumero(String numero) {
        for (Factura f : facturas) {
            if (f.getNumero().equals(numero)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Devuelve las facturas de un tipo dado ("A", "B" o "C").
     *
     * TODO PASO 4 (Streams): migrar el for + if a un filter de Streams:
     *       return facturas.stream()
     *               .filter(f -> f.getTipo().equals(tipo))
     *               .toList();
     */
    public List<Factura> buscarPorTipo(String tipo) {
        List<Factura> resultado = new ArrayList<>();
        for (Factura f : facturas) {
            if (f.getTipo().equals(tipo)) {
                resultado.add(f);
            }
        }
        return resultado;
    }
}
