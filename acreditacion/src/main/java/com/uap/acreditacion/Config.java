package com.uap.acreditacion;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import java.awt.image.BufferedImage;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

public class Config {

	private final Logger log = LoggerFactory.getLogger(getClass());

	public String guardarArchivo(MultipartFile archivo) {
		String uniqueFilename = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();

		Path rootPath = Paths.get("uploads/").resolve(uniqueFilename);
		Path rootAbsolutPath = rootPath.toAbsolutePath();
		System.out.println("LA DIRECCION ES: " + rootAbsolutPath);
		log.info("rootPath: " + rootPath);
		log.info("rootAbsolutPath: " + rootAbsolutPath);

		try {
			System.out.println("CUARDAR EN EL DIRECCTORIO");
			Files.copy(archivo.getInputStream(), rootAbsolutPath);

		} catch (IOException e) {
			System.out.println("ERROR AL GUARDAR EL ARCHIVO: " + e.getMessage());
			e.printStackTrace();
		}

		return uniqueFilename;
	}

	public String generarIconoDePdf(MultipartFile file) {
		
		String uniqueFileName = UUID.randomUUID().toString() + "_ICONO.jpg";
		Path projectPath = Paths.get("uploads/").resolve(uniqueFileName);
		Path rootAbsolutPath = projectPath.toAbsolutePath();
		String iconFilePath = rootAbsolutPath.toString();

		try {
			// Leer el archivo PDF para generar el ícono
			byte[] fileBytes = file.getBytes();
			PDDocument document = PDDocument.load(fileBytes);
			PDFRenderer renderer = new PDFRenderer(document);

			// Convertir la primera página a imagen
			BufferedImage image = renderer.renderImageWithDPI(0, 300);

			// Obtener la mitad superior de la imagen
			int height = image.getHeight();
			int width = image.getWidth();
			BufferedImage topHalfImage = image.getSubimage(0, 0, width, height / 3);

			// Guardar la imagen en formato JPG
			File iconFile = new File(iconFilePath);
			ImageIO.write(topHalfImage, "jpg", iconFile);

			// Cerrar el documento
			document.close();

			System.out.println("LA DIRECCION DEL ICONO ES: " + rootAbsolutPath);
			// Devolver el nombre del archivo del ícono
			return uniqueFileName;
		} catch (IOException e) {
			e.printStackTrace();
			return null; // O lanza una excepción según tu manejo de errores
		}

	}
}