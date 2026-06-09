package com.erp.microerp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns(
                        "/home",
                        "/clientes/**",
                        "/fornecedores/**",
                        "/produtos/**",
                        "/compras/**",
                        "/vendas/**",
                        "/estoque/**",
                        "/relatorios/**",
                        "/plano-contas/**",
                        "/financeiro/**",
                        "/contas-financeiras/**",
                        "/perdas/**",
                        "/dashboard/**",
                        "/auditoria/**",
                        "/fluxo-caixa/**",
                        "/usuarios/**",
                        "/dre/**",
                        "/balanco/**",
                        "/lucratividade/**"
                )
                .excludePathPatterns(
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                );
    }
}