package com.buyuphk.soketcommunication.rxretrofit.responseresult;

import java.util.List;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2020-03-11 09:00
 * motto: 勇于向未知领域探索
 */
public class LoginResult {
    private String success;
    private String expcountshos_onepage;
    private String grade;
    private List<InfoEntity> info;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getExpcountshos_onepage() {
        return expcountshos_onepage;
    }

    public void setExpcountshos_onepage(String expcountshos_onepage) {
        this.expcountshos_onepage = expcountshos_onepage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public List<InfoEntity> getInfo() {
        return info;
    }

    public void setInfo(List<InfoEntity> info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "success='" + success + '\'' +
                ", expcountshos_onepage='" + expcountshos_onepage + '\'' +
                ", grade='" + grade + '\'' +
                ", info=" + info +
                '}';
    }

    public class InfoEntity {
        private String storagerackline;
        private String lineproperty;

        public String getStoragerackline() {
            return storagerackline;
        }

        public void setStoragerackline(String storagerackline) {
            this.storagerackline = storagerackline;
        }

        public String getLineproperty() {
            return lineproperty;
        }

        public void setLineproperty(String lineproperty) {
            this.lineproperty = lineproperty;
        }

        @Override
        public String toString() {
            return "InfoEntity{" +
                    "storagerackline='" + storagerackline + '\'' +
                    ", lineproperty='" + lineproperty + '\'' +
                    '}';
        }
    }
}
