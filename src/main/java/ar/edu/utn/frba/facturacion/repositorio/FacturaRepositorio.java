package ar.edu.utn.frba.facturacion.repositorio;

import ar.edu.utn.frba.facturacion.modelo.CalculadoraIva;
import ar.edu.utn.frba.facturacion.modelo.Factura;
import ar.edu.utn.frba.facturacion.modelo.FacturaA;
import ar.edu.utn.frba.facturacion.modelo.FacturaB;
import ar.edu.utn.frba.facturacion.modelo.FacturaC;
import ar.edu.utn.frba.facturacion.modelo.FacturaE;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio en memoria de facturas.
 *
 * Version MIGRADA: usa Streams para consultar y devuelve Optional en vez
 * de null cuando la busqueda puede no encontrar nada.
 */
@Repository
public class FacturaRepositorio {

    private final List<Factura> facturas = List.of(
            new FacturaA("A-0001", 10000.0, "30-71234567-8", LocalDate.of(2026, 1, 15)),
            new FacturaA("A-0002", 25000.0, "30-59876543-2", LocalDate.of(2026, 2, 20)),
            new FacturaA("A-0003", 40000.0, "30-68888888-8", LocalDate.of(2026, 5, 8)),
            new FacturaB("B-0001", 5000.0, "Juan Perez", LocalDate.of(2026, 1, 10)),
            new FacturaB("B-0002", 8000.0, "Ana Gomez", LocalDate.of(2026, 3, 5)),
            new FacturaC("C-0001", 3000.0, LocalDate.of(2026, 2, 28)),
            new FacturaE("E-0001", 15000.0, "Brasil", LocalDate.of(2026, 3, 20))
    );

    /** Todas las facturas. */
    public List<Factura> buscarTodas() {
        return facturas.stream().toList();
    }

    /** Busca por numero; Optional vacio si no existe (ya no devuelve null). */
    public Optional<Factura> buscarPorNumero(String numero) {
        return facturas.stream()
                .filter(f -> f.numero().equals(numero))
                .findFirst();
    }

    /** Facturas de un tipo dado ("A", "B", "C" o "E"). */
    public List<Factura> buscarPorTipo(String tipo) {
        return facturas.stream()
                .filter(f -> CalculadoraIva.tipo(f).equals(tipo))
                .toList();
    }
}
