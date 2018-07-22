package com.blako.mensajero.models.inbound;

import com.blako.mensajero.models.Fence;

import java.util.List;

public class ZonesInnerZonesResponse {
    private Integer count;
    private Integer jitter;
    private Integer lifespan;
    private Integer revision;
    private Double fallback;
    private Long start;
    private List<Fence> fences;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getJitter() {
        return jitter;
    }

    public void setJitter(Integer jitter) {
        this.jitter = jitter;
    }

    public Integer getLifespan() {
        return lifespan;
    }

    public void setLifespan(Integer lifespan) {
        this.lifespan = lifespan;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Double getFallback() {
        return fallback;
    }

    public void setFallback(Double fallback) {
        this.fallback = fallback;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public List<Fence> getFences() {
        return fences;
    }

    public void setFences(List<Fence> fences) {
        this.fences = fences;
    }
}
