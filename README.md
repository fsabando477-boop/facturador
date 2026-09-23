# Facturador SRI - Semanas 5 y 6

Aplicacion de escritorio en Java + Swing que arma una **factura electronica valida
para el SRI del Ecuador** desde una interfaz grafica, aplicando los conceptos
vistos en las semanas anteriores:

- **Encapsulamiento**: todos los atributos son privados; los setters replican las
  restricciones del XSD (`Validaciones.java`).
- **Herencia**: `Impuesto` -> `Iva | Ice | Irbpnr`; `Cliente` -> `ClienteMinorista |
  ClienteMayorista`; `ComprobanteElectronico` -> `Factura`.
- **Polimorfismo**: la factura llama a `cliente.calcularDescuento(...)` y a
  `impuesto.calcularValor()` sin saber la clase concreta.
- **Semana 5 - Colecciones y genericos**: se usan las tres colecciones que pide
  la tarea:
  - `ArrayList<Detalle>` en `Pantalla` para el catalogo de productos.
  - `HashSet<String>` en `Pantalla` para evitar codigos duplicados en O(1).
  - `HashMap<String, ComprobanteElectronico>` (LinkedHashMap) en
    `RegistroComprobantesEnMemoria`.
  Ademas hay `List<Impuesto>`, `List<Pago>` y `List<CampoAdicional>` dentro del
  modelo.
- **Semana 6 - Interfaz grafica y eventos**: `Pantalla.java` con Swing en
  pestanas, botones para las 5 operaciones CRUD (Agregar, Editar, Eliminar,
  Buscar, Limpiar) sobre el catalogo y validaciones con `JOptionPane`.

## Estructura del codigo

```
com.fsabando.facturador
 |- Facturador.java              (main)
 |- catalogo/                    (enums de SRI: Ambiente, TipoEmision, ...)
 |- comun/Validaciones.java      (utilidades de validacion del XSD)
 |- modelo/                      (Cliente, Detalle, InfoTributaria, ...)
 |   |- impuesto/                (Impuesto abstract + IVA/ICE/IRBPNR)
 |   `- comprobante/             (ComprobanteElectronico + Factura)
 |- registro/                    (RegistroComprobantes + implementacion)
 |- xml/GeneradorXmlFactura.java (serializa a XML segun factura_V1.1.0.xsd)
 `- ui/Pantalla.java             (Swing con eventos y CRUD de detalles)
```

## Requisitos

- JDK 17 o superior.
- Maven 3.6+ (opcional; tambien se puede compilar con `javac`).

## Ejecucion

Con Maven:

```
mvn -q compile exec:java
```

Sin Maven:

```
mkdir -p target/classes
javac -d target/classes --release 17 $(find src/main/java -name "*.java")
java -cp target/classes com.fsabando.facturador.Facturador
```

## Flujo de uso

1. **Pestana Emisor**: cargar RUC (13 digitos, termina en `001`),
   establecimiento, punto de emision y secuencial.
2. **Pestana Factura**: fecha de emision en formato `dd/MM/yyyy`, direccion del
   establecimiento y obligado a contabilidad.
3. **Pestana Comprador**: tipo y numero de identificacion (con validacion de
   digito verificador para cedulas y RUC), razon social y direccion. Se elige
   ademas si el cliente es **Minorista** o **Mayorista** (aqui se demuestra el
   polimorfismo del descuento).
4. **Pestana Detalles**: catalogo de productos con las 5 operaciones CRUD:
   - **Agregar**: abre el dialogo para crear un producto. Si el codigo ya
     existe en el `HashSet`, se rechaza.
   - **Editar**: reabre el dialogo con la fila seleccionada prellenada.
   - **Eliminar**: quita la fila del catalogo y libera su codigo.
   - **Buscar**: por codigo o descripcion; selecciona y desplaza la tabla al
     resultado.
   - **Limpiar tabla**: vacia el catalogo.
5. **Pestana Pago**: elegir la forma de pago del SRI.
6. Con **Emitir factura y generar XML** la aplicacion:
   - Construye el modelo (`Factura`, `Detalle`, `Pago`, ...).
   - Valida las reglas del XSD.
   - Genera la clave de acceso de 49 digitos (modulo 11 del SRI).
   - Guarda el comprobante en el registro en memoria y produce el XML.
   - Solicita la ubicacion donde guardar el archivo `.xml`.

## Notas

- Los datos precargados son referenciales para acelerar la demostracion; se
  pueden reemplazar libremente.
- El XML producido cumple con la estructura del esquema `factura_V1.1.0.xsd`
  del SRI (namespaces, orden de elementos, formato de fechas y decimales,
  clave de acceso con digito verificador modulo 11).
