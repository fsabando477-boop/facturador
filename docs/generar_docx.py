"""
Genera el documento de entrega (docx) para las Semanas 5 y 6.
Requiere: python-docx (pip install python-docx).
"""

from pathlib import Path

from docx import Document
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.shared import Pt, Cm, RGBColor

SALIDA = Path(__file__).with_name("Sabando_Francisco_Semanas5y6.docx")

DATOS = {
    "nombre": "Francisco Sabando",
    "materia": "Programacion Funcional",
    "universidad": "Universidad Espiritu Santo (UEES)",
    "fecha": "22 de septiembre de 2026",
    "repo": "https://github.com/<tu-usuario>/facturador-sri",
}


def h1(doc, texto):
    p = doc.add_paragraph()
    r = p.add_run(texto)
    r.bold = True
    r.font.size = Pt(16)
    r.font.color.rgb = RGBColor(0x1F, 0x3A, 0x93)


def h2(doc, texto):
    p = doc.add_paragraph()
    r = p.add_run(texto)
    r.bold = True
    r.font.size = Pt(13)
    r.font.color.rgb = RGBColor(0x1F, 0x3A, 0x93)


def parrafo(doc, texto, negrita=False):
    p = doc.add_paragraph()
    r = p.add_run(texto)
    r.font.size = Pt(11)
    if negrita:
        r.bold = True


def vinieta(doc, texto):
    p = doc.add_paragraph(style="List Bullet")
    r = p.add_run(texto)
    r.font.size = Pt(11)


def enumerado(doc, texto):
    p = doc.add_paragraph(style="List Number")
    r = p.add_run(texto)
    r.font.size = Pt(11)


def bloque_codigo(doc, texto):
    p = doc.add_paragraph()
    r = p.add_run(texto)
    r.font.name = "Consolas"
    r.font.size = Pt(9)


def portada(doc):
    for _ in range(6):
        doc.add_paragraph()
    titulo = doc.add_paragraph()
    titulo.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = titulo.add_run("Semanas 5 y 6\nColecciones, Genericos, Interfaz Grafica y Manejo de Eventos")
    r.bold = True
    r.font.size = Pt(22)
    r.font.color.rgb = RGBColor(0x1F, 0x3A, 0x93)

    subt = doc.add_paragraph()
    subt.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = subt.add_run("Facturador SRI en Java + Swing")
    r.font.size = Pt(14)

    for _ in range(6):
        doc.add_paragraph()

    for etiqueta, valor in [
        ("Estudiante:", DATOS["nombre"]),
        ("Materia:", DATOS["materia"]),
        ("Institucion:", DATOS["universidad"]),
        ("Fecha de entrega:", DATOS["fecha"]),
    ]:
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        r = p.add_run(f"{etiqueta} {valor}")
        r.font.size = Pt(12)
        if etiqueta == "Estudiante:":
            r.bold = True

    doc.add_page_break()


def seccion_resumen(doc):
    h1(doc, "1. Resumen de la solucion")
    parrafo(
        doc,
        "La entrega consiste en una aplicacion de escritorio en Java + Swing que "
        "permite armar una factura electronica valida para el SRI del Ecuador. "
        "La interfaz recolecta los datos del emisor, la factura, el comprador, "
        "los detalles y la forma de pago; a partir de ellos construye el modelo "
        "de dominio, valida las reglas del esquema XSD, genera la clave de "
        "acceso de 49 digitos (algoritmo modulo 11 del SRI) y produce el XML "
        "correspondiente que puede guardarse en disco.",
    )
    parrafo(
        doc,
        "El proyecto integra en una unica entrega los temas exigidos por las "
        "semanas 5 y 6: colecciones y genericos por un lado, e interfaz grafica "
        "con manejo de eventos por otro, sobre las clases desarrolladas en las "
        "semanas anteriores (encapsulamiento, herencia y polimorfismo).",
    )


def seccion_conceptos(doc):
    h1(doc, "2. Conceptos aplicados")

    h2(doc, "2.1 Encapsulamiento")
    parrafo(
        doc,
        "Todos los atributos del modelo son privados y cada setter replica la "
        "restriccion declarada en el XSD del SRI. La clase Validaciones "
        "centraliza las comprobaciones (longitud minima y maxima, patrones "
        "regex, decimales no negativos, digito verificador de cedula y RUC).",
    )

    h2(doc, "2.2 Herencia")
    vinieta(doc, "Impuesto (abstracta) -> Iva, Ice, Irbpnr.")
    vinieta(doc, "Cliente (abstracta) -> ClienteMinorista, ClienteMayorista.")
    vinieta(doc, "ComprobanteElectronico (abstracta) -> Factura.")

    h2(doc, "2.3 Polimorfismo")
    parrafo(
        doc,
        "La factura no conoce el tipo real del cliente ni del impuesto: llama a "
        "cliente.calcularDescuento(...) y a impuesto.calcularValor(). Cada "
        "subclase resuelve el calculo a su manera (por tramos para el "
        "minorista, contractual mas cupo para el mayorista, porcentaje ad "
        "valorem para IVA e ICE, tarifa fija por unidad para el IRBPNR).",
    )

    h2(doc, "2.4 Colecciones y genericos (Semana 5)")
    vinieta(doc, "List<Detalle>, List<Pago>, List<Impuesto>, List<CampoAdicional> en el modelo.")
    vinieta(
        doc,
        "Map<String, ComprobanteElectronico> en RegistroComprobantesEnMemoria "
        "(LinkedHashMap para conservar el orden de emision).",
    )
    vinieta(
        doc,
        "La interfaz RegistroComprobantes es la abstraccion, y "
        "RegistroComprobantesEnMemoria una implementacion; se puede sustituir "
        "por otra sin tocar el codigo que emite comprobantes.",
    )
    vinieta(
        doc,
        "Evitar duplicados: el registro rechaza dos comprobantes con la misma "
        "clave de acceso (unicidad garantizada por el Map).",
    )
    vinieta(
        doc,
        "Se aplican las cinco operaciones CRUD sobre la lista de detalles "
        "desde la interfaz: crear, listar, actualizar, eliminar y buscar.",
    )

    h2(doc, "2.5 Interfaz grafica y manejo de eventos (Semana 6)")
    vinieta(doc, "Ventana JFrame organizada en JTabbedPane con cinco pestanas.")
    vinieta(doc, "Botones con ActionListener para agregar, eliminar y limpiar detalles.")
    vinieta(
        doc,
        "JOptionPane para dialogos de agregar producto y mensajes de validacion.",
    )
    vinieta(
        doc,
        "JFileChooser para elegir la ubicacion del XML resultante.",
    )
    vinieta(
        doc,
        "Las excepciones del modelo (IllegalArgumentException, "
        "IllegalStateException) se muestran al usuario con mensajes claros.",
    )


def seccion_estructura(doc):
    h1(doc, "3. Estructura del codigo")
    bloque_codigo(
        doc,
        (
            "com.fsabando.facturador\n"
            " |- Facturador.java              (main)\n"
            " |- catalogo/                    (enums SRI: Ambiente, TipoEmision, ...)\n"
            " |- comun/Validaciones.java      (utilidades del XSD)\n"
            " |- modelo/                      (Cliente, Detalle, InfoTributaria, ...)\n"
            " |   |- impuesto/                (Impuesto abstract + IVA/ICE/IRBPNR)\n"
            " |   `- comprobante/             (ComprobanteElectronico + Factura)\n"
            " |- registro/                    (RegistroComprobantes + implementacion)\n"
            " |- xml/GeneradorXmlFactura.java (serializa a XML segun XSD 1.1.0)\n"
            " `- ui/Pantalla.java             (Swing con eventos y CRUD de detalles)\n"
        ),
    )


def seccion_flujo(doc):
    h1(doc, "4. Flujo de uso de la aplicacion")
    enumerado(doc, "Pestana Emisor: cargar RUC, establecimiento, punto de emision y secuencial.")
    enumerado(doc, "Pestana Factura: fecha de emision, direccion del establecimiento y obligado a contabilidad.")
    enumerado(
        doc,
        "Pestana Comprador: tipo y numero de identificacion (con validacion de digito "
        "verificador para cedulas y RUC), razon social y direccion. Se elige ademas si el "
        "cliente es Minorista o Mayorista para demostrar el polimorfismo del descuento.",
    )
    enumerado(
        doc,
        "Pestana Detalles: Agregar producto abre un dialogo que crea un Detalle validado "
        "y lo agrega a la tabla. Tambien puede Eliminar seleccionado o Limpiar tabla "
        "(CRUD sobre la coleccion).",
    )
    enumerado(doc, "Pestana Pago: elegir la forma de pago del SRI.")
    enumerado(
        doc,
        "Boton 'Emitir factura y generar XML': construye el modelo, lo valida, calcula "
        "la clave de acceso (modulo 11), guarda el comprobante en el registro y solicita "
        "la ubicacion donde escribir el archivo .xml.",
    )


def seccion_ejecucion(doc):
    h1(doc, "5. Ejecucion")
    parrafo(doc, "Con Maven:")
    bloque_codigo(doc, "mvn -q compile exec:java")
    parrafo(doc, "Sin Maven, directamente con el JDK:")
    bloque_codigo(
        doc,
        (
            "mkdir -p target/classes\n"
            "javac -d target/classes --release 17 $(find src/main/java -name \"*.java\")\n"
            "java -cp target/classes com.fsabando.facturador.Facturador\n"
        ),
    )


def seccion_capturas(doc):
    h1(doc, "6. Capturas de pantalla")
    parrafo(
        doc,
        "Insertar aqui las capturas de las pestanas (Emisor, Factura, Comprador, "
        "Detalles, Pago), del dialogo de agregar producto, del mensaje de emision "
        "exitosa y del XML resultante abierto en un editor.",
    )
    for etiqueta in [
        "Captura 1: Pestana Emisor con los datos tributarios.",
        "Captura 2: Pestana Detalles con productos agregados en la tabla.",
        "Captura 3: Dialogo Agregar producto.",
        "Captura 4: Mensaje de factura emitida con la clave de acceso.",
        "Captura 5: Contenido del XML generado.",
    ]:
        p = doc.add_paragraph()
        r = p.add_run("[Espacio para " + etiqueta + "]")
        r.italic = True
        r.font.size = Pt(11)


def seccion_repo(doc):
    h1(doc, "7. Repositorio")
    parrafo(doc, "Codigo fuente completo en:")
    p = doc.add_paragraph()
    r = p.add_run(DATOS["repo"])
    r.font.size = Pt(12)
    r.bold = True
    parrafo(
        doc,
        "El repositorio incluye pom.xml, codigo fuente en src/main/java y un README.md "
        "con las instrucciones de compilacion y ejecucion.",
    )


def main():
    doc = Document()

    seccion_normal = doc.styles["Normal"]
    seccion_normal.font.name = "Calibri"
    seccion_normal.font.size = Pt(11)

    for section in doc.sections:
        section.top_margin = Cm(2)
        section.bottom_margin = Cm(2)
        section.left_margin = Cm(2.5)
        section.right_margin = Cm(2.5)

    portada(doc)
    seccion_resumen(doc)
    seccion_conceptos(doc)
    seccion_estructura(doc)
    seccion_flujo(doc)
    seccion_ejecucion(doc)
    seccion_capturas(doc)
    seccion_repo(doc)

    doc.save(SALIDA)
    print(f"Documento generado: {SALIDA}")


if __name__ == "__main__":
    main()
