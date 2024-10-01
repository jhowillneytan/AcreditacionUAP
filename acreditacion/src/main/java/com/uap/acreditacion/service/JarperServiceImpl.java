package com.uap.acreditacion.service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service
public class JarperServiceImpl implements JarperService {

    @Autowired
    private DataSource dataSource;

    @Override
    public ByteArrayOutputStream compilarAndExportarReporte(String nombreArchivo, Map<String, Object> params)
            throws IOException, JRException, SQLException {
        Connection con = null;

        // return stream;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Path rootPath = Paths.get("").toAbsolutePath();
        Path directorio = Paths.get(rootPath.toString(), "reportes", nombreArchivo);
        String ruta = directorio.toString();

        try (InputStream reportStream = new FileInputStream(ruta)) {
            con = dataSource.getConnection();

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, con);
            JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
        } catch (IOException | JRException | SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        con.close();
        return stream;

    }

    @Override
    public ByteArrayOutputStream compilarAndExportarReporteExcel(String nombreArchivo, Map<String, Object> params)
            throws IOException, JRException, SQLException {
        Connection con = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Path rootPath = Paths.get("").toAbsolutePath();
        Path directorio = Paths.get(rootPath.toString(), "reportes", nombreArchivo);
        String ruta = directorio.toString();

        try (InputStream reportStream = new FileInputStream(ruta)) {
            con = dataSource.getConnection();

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, con);

            // Exportar a Excel
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(stream));

            exporter.exportReport();

        } catch (IOException | JRException | SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
        }

        return stream;
    }

    @Override
    public ByteArrayOutputStream compilarAndExportarReporteWord(String nombreArchivo, Map<String, Object> params)
            throws IOException, JRException, SQLException {
        Connection con = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Path rootPath = Paths.get("").toAbsolutePath();
        Path directorio = Paths.get(rootPath.toString(), "reportes", nombreArchivo);
        String ruta = directorio.toString();

        try (InputStream reportStream = new FileInputStream(ruta)) {
            con = dataSource.getConnection();

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, con);

            // Exportar a RTF
            JRDocxExporter exporter = new JRDocxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(stream));

            exporter.exportReport();

        } catch (IOException | JRException | SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
        }

        return stream;
    }

    @Override
    public ByteArrayOutputStream compilarAndExportarReporteExcel(String nombreArchivo, Map<String, Object> params,
            JRDataSource dataSource)
            throws IOException, JRException, SQLException {
        Connection con = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Path rootPath = Paths.get("").toAbsolutePath();
        Path directorio = Paths.get(rootPath.toString(), "reportes", nombreArchivo);
        String ruta = directorio.toString();

        try (InputStream reportStream = new FileInputStream(ruta)) {
            // Si se necesita una conexión de base de datos
            if (dataSource == null) {
                con = this.dataSource.getConnection(); // Aquí asumo que tienes un dataSource para conexiones a DB
                // Llenar el reporte usando la conexión
                JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, con);

                // Exportar a Excel
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(stream));

                exporter.exportReport();
            } else {
                // Llenar el reporte usando el JRDataSource
                JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

                // Exportar a Excel
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(stream));

                exporter.exportReport();
            }

        } catch (IOException | JRException | SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
        }

        return stream;
    }

}
