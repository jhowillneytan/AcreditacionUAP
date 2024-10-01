package com.uap.acreditacion.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;

public interface JarperService {

        ByteArrayOutputStream compilarAndExportarReporte(String ruta, Map<String, Object> params)
                        throws IOException, JRException, SQLException;

        ByteArrayOutputStream compilarAndExportarReporteExcel(String ruta, Map<String, Object> params)
                        throws IOException, JRException, SQLException;

        public ByteArrayOutputStream compilarAndExportarReporteWord(String nombreArchivo, Map<String, Object> params)
                        throws IOException, JRException, SQLException;

        public ByteArrayOutputStream compilarAndExportarReporteExcel(String nombreArchivo, Map<String, Object> params,
                        JRDataSource dataSource)
                        throws IOException, JRException, SQLException;
}
