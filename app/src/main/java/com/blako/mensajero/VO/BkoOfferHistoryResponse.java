package com.blako.mensajero.VO;

import java.util.ArrayList;

/**
 * Created by franciscotrinidad on 1/26/16.
 */
public class BkoOfferHistoryResponse extends BkoRequestResponse {

    private ArrayList<BkoOfferHistoryVO> servicios;

    public ArrayList<BkoOfferHistoryVO> getServicios() {
        return servicios;
    }

    public void setServicios(ArrayList<BkoOfferHistoryVO> servicios) {
        this.servicios = servicios;
    }

    public class BkoOfferHistoryVO {

        private String inicio_fecha_offert;
        private String fin_fecha_offert;
        private String cliente;
        private String almacen;
        private String ciudad;
        private String tipo_servicio;
        private String fecha_checkin;
        private String fecha_checkout;
        private String bid_offert;
        private String HG_offert;
        private String generado;
        private String garantizado;
        private String pago;
        private String status_pago;
        private String worker;
        private String clasificacion;
        private String descripcion_clasificacion;
        private String descripcion_corta;
        private String servicios;

        public String getServicios() {
            return servicios;
        }

        public void setServicios(String servicios) {
            this.servicios = servicios;
        }

        public String getInicio_fecha_offert() {
            return inicio_fecha_offert;
        }

        public void setInicio_fecha_offert(String inicio_fecha_offert) {
            this.inicio_fecha_offert = inicio_fecha_offert;
        }

        public String getFin_fecha_offert() {
            return fin_fecha_offert;
        }

        public void setFin_fecha_offert(String fin_fecha_offert) {
            this.fin_fecha_offert = fin_fecha_offert;
        }

        public String getCliente() {
            return cliente;
        }

        public void setCliente(String cliente) {
            this.cliente = cliente;
        }

        public String getAlmacen() {
            return almacen;
        }

        public void setAlmacen(String almacen) {
            this.almacen = almacen;
        }

        public String getCiudad() {
            return ciudad;
        }

        public void setCiudad(String ciudad) {
            this.ciudad = ciudad;
        }

        public String getTipo_servicio() {
            return tipo_servicio;
        }

        public void setTipo_servicio(String tipo_servicio) {
            this.tipo_servicio = tipo_servicio;
        }

        public String getFecha_checkin() {
            return fecha_checkin;
        }

        public void setFecha_checkin(String fecha_checkin) {
            this.fecha_checkin = fecha_checkin;
        }

        public String getFecha_checkout() {
            return fecha_checkout;
        }

        public void setFecha_checkout(String fecha_checkout) {
            this.fecha_checkout = fecha_checkout;
        }

        public String getBid_offert() {
            return bid_offert;
        }

        public void setBid_offert(String bid_offert) {
            this.bid_offert = bid_offert;
        }

        public String getHG_offert() {
            return HG_offert;
        }

        public void setHG_offert(String HG_offert) {
            this.HG_offert = HG_offert;
        }

        public String getGenerado() {
            return generado;
        }

        public void setGenerado(String generado) {
            this.generado = generado;
        }

        public String getGarantizado() {
            return garantizado;
        }

        public void setGarantizado(String garantizado) {
            this.garantizado = garantizado;
        }

        public String getPago() {
            return pago;
        }

        public void setPago(String pago) {
            this.pago = pago;
        }

        public String getStatus_pago() {
            return status_pago;
        }

        public void setStatus_pago(String status_pago) {
            this.status_pago = status_pago;
        }

        public String getWorker() {
            return worker;
        }

        public void setWorker(String worker) {
            this.worker = worker;
        }

        public String getClasificacion() {
            return clasificacion;
        }

        public void setClasificacion(String clasificacion) {
            this.clasificacion = clasificacion;
        }

        public String getDescripcion_clasificacion() {
            return descripcion_clasificacion;
        }

        public void setDescripcion_clasificacion(String descripcion_clasificacion) {
            this.descripcion_clasificacion = descripcion_clasificacion;
        }

        public String getDescripcion_corta() {
            return descripcion_corta;
        }

        public void setDescripcion_corta(String descripcion_corta) {
            this.descripcion_corta = descripcion_corta;
        }
    }


}
