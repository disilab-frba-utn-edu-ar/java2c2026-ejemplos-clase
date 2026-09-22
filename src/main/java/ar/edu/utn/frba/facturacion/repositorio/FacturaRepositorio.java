package ar.edu.utn.frba.facturacion.repositorio;

import ar.edu.utn.frba.facturacion.modelo.Factura;
import ar.edu.utn.frba.facturacion.modelo.FacturaA;
import ar.edu.utn.frba.facturacion.modelo.FacturaB;
import ar.edu.utn.frba.facturacion.modelo.FacturaC;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
        facturas.add(new FacturaA("A-0001", 10000.0, "30-71234567-8", LocalDate.of(2026, 1, 15)));
        facturas.add(new FacturaA("A-0002", 25000.0, "30-59876543-2", LocalDate.of(2026, 2, 20)));
        facturas.add(new FacturaA("A-0003", 40000.0, "30-68888888-8", LocalDate.of(2026, 5, 8)));
        facturas.add(new FacturaB("B-0001", 5000.0, "Juan Perez", LocalDate.of(2026, 1, 10)));
        facturas.add(new FacturaB("B-0002", 8000.0, "Ana Gomez", LocalDate.of(2026, 3, 5)));
        facturas.add(new FacturaC("C-0001", 3000.0, LocalDate.of(2026, 2, 28)));
    }

    public List<Factura> buscarTodas() {
        return facturas;
    }

    /**
     * TODO PASO 5 (Optional):
     *   Migrar la firma a Optional<Factura> y resolver con Streams
     *
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
     *
     * TODO PASO 4 (Streams): reemplazar el 
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
