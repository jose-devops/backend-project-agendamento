package com.api.app.models.enums;

public enum TipoAcesso {
    PROPRIETARIO,
    MORADOR;

    public boolean isProprietario() {
        return this == PROPRIETARIO;
    }

    public boolean isMorador() {
        return this == MORADOR;
    }
}
