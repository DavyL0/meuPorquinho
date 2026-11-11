package com.meu.porquinho.service;

import com.meu.porquinho.model.Investimento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Envio de e-mails transacionais.
 * Se o SMTP não estiver configurado (MAIL_HOST vazio), apenas registra em log —
 * assim o desenvolvimento local não depende de um servidor de e-mail.
 */
@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final boolean configurado;
    private final String remetente;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender,
                        @Value("${spring.mail.host:}") String host,
                        @Value("${app.mail-from}") String remetente) {
        this.mailSender = mailSender;
        this.configurado = mailSender != null && host != null && !host.isBlank();
        this.remetente = remetente;
    }

    public void enviarAlertaInvestimento(Investimento inv, String emailDestino,
                                         BigDecimal cotacaoAtual, BigDecimal variacaoPct) {
        NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        String direcao = variacaoPct.signum() >= 0 ? "subiu" : "caiu";

        BigDecimal valorInvestido = inv.getValorInicial().multiply(inv.getQuantidade());
        BigDecimal valorAtual = cotacaoAtual.multiply(inv.getQuantidade());

        String assunto = String.format("🐷 Alerta: %s %s %.2f%%",
                inv.getSimbolo(), direcao, variacaoPct.abs());

        String corpo = """
                Olá!

                Seu alerta de investimento foi disparado.

                Ativo:              %s
                Quantidade:         %s
                Preço pago (unit.): %s
                Cotação atual:      %s
                Variação:           %s%%

                Valor investido:    %s
                Valor atual:        %s

                Acesse o Porquinho para mais detalhes.
                """.formatted(
                inv.getSimbolo(),
                inv.getQuantidade().stripTrailingZeros().toPlainString(),
                moeda.format(inv.getValorInicial()),
                moeda.format(cotacaoAtual),
                variacaoPct.setScale(2, RoundingMode.HALF_UP),
                moeda.format(valorInvestido),
                moeda.format(valorAtual)
        );

        enviar(emailDestino, assunto, corpo);
    }

    public void enviar(String para, String assunto, String corpo) {
        if (!configurado) {
            log.warn("SMTP não configurado (MAIL_HOST vazio). E-mail não enviado. "
                    + "Para: {} | Assunto: {}", para, assunto);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(remetente);
            msg.setTo(para);
            msg.setSubject(assunto);
            msg.setText(corpo);
            mailSender.send(msg);
            log.info("E-mail enviado para {}: {}", para, assunto);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para {}: {}", para, e.getMessage());
        }
    }
}
