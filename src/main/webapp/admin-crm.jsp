<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Autorizar CRM - ADMIN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="table-container" style="max-width: 900px;">
        <h1 style="font-size: 2em; margin-bottom: 30px;">🏥 Autorizar CRM Médico</h1>

        <c:if test="${sessionScope.usuarioCategoria != 0}">
            <div class="erro">
                <strong>❌ Acesso Restrito:</strong> Área exclusiva para Administradores.
            </div>
            <a href="${pageContext.request.contextPath}/index.jsp" class="voltar-link">Voltar ao Início</a>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria == 0}">

            <c:if test="${not empty erro}">
                <div class="erro">
                    <strong>❌ Erro:</strong> ${erro}
                </div>
                <c:remove var="erro" scope="session"/>
            </c:if>

            <c:if test="${not empty sucesso}">
                <div class="sucesso">
                    <strong>✓ Sucesso:</strong> ${sucesso}
                </div>
                <c:remove var="sucesso" scope="session"/>
            </c:if>

            <div style="margin-bottom: 30px; padding: 15px; background: rgba(0, 212, 255, 0.1); border-radius: 5px; border-left: 4px solid #00d4ff;">
                <strong>ℹ️ Informação:</strong> Cadastre um CRM autorizado e o email do médico. 
                Somente médicos com CRM + email pré-autorizados poderão criar conta no sistema.
                <br><small style="color: #aaa;">Formato: CRM/UF XXXXXX (ex: CRM/SP 123456)</small>
            </div>

            <!-- Formulário de Cadastro de CRM -->
            <form method="POST" action="${pageContext.request.contextPath}/CadastrarCrmServlet">
                <div style="display: grid; grid-template-columns: 1fr 1fr 2fr; gap: 15px; align-items: end;">
                    <div class="form-group" style="margin-bottom: 0;">
                        <label for="crm_numero">Número CRM:</label>
                        <input type="text" id="crm_numero" name="crm_numero" required 
                               maxlength="6" placeholder="123456" 
                               oninput="this.value = this.value.replace(/\D/g, '')"
                               style="text-align: center; font-size: 1.1em; letter-spacing: 2px;">
                    </div>

                    <div class="form-group" style="margin-bottom: 0;">
                        <label for="crm_uf">UF:</label>
                        <select id="crm_uf" name="crm_uf" required style="font-size: 1.1em; text-align: center;">
                            <option value="" disabled selected>UF</option>
                            <option value="AC">AC</option>
                            <option value="AL">AL</option>
                            <option value="AP">AP</option>
                            <option value="AM">AM</option>
                            <option value="BA">BA</option>
                            <option value="CE">CE</option>
                            <option value="DF">DF</option>
                            <option value="ES">ES</option>
                            <option value="GO">GO</option>
                            <option value="MA">MA</option>
                            <option value="MT">MT</option>
                            <option value="MS">MS</option>
                            <option value="MG">MG</option>
                            <option value="PA">PA</option>
                            <option value="PB">PB</option>
                            <option value="PR">PR</option>
                            <option value="PE">PE</option>
                            <option value="PI">PI</option>
                            <option value="RJ">RJ</option>
                            <option value="RN">RN</option>
                            <option value="RS">RS</option>
                            <option value="RO">RO</option>
                            <option value="RR">RR</option>
                            <option value="SC">SC</option>
                            <option value="SP">SP</option>
                            <option value="SE">SE</option>
                            <option value="TO">TO</option>
                        </select>
                    </div>

                    <div class="form-group" style="margin-bottom: 0;">
                        <label for="email_autorizado">Email do Médico:</label>
                        <input type="email" id="email_autorizado" name="email_autorizado" required 
                               placeholder="medico@email.com">
                    </div>
                </div>

                <button type="submit" class="btn-submit" style="margin-top: 20px;">
                    ✅ Autorizar CRM
                </button>
            </form>

            <hr style="border: 1px solid rgba(255, 255, 255, 0.2); margin: 30px 0;">

            <h2 style="color: #00d4ff; margin-bottom: 20px;">CRMs Autorizados</h2>

            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 2px solid #00d4ff;">
                            <th style="padding: 12px; text-align: left;">CRM</th>
                            <th style="padding: 12px; text-align: left;">Email Autorizado</th>
                            <th style="padding: 12px; text-align: left;">Status</th>
                            <th style="padding: 12px; text-align: left;">Data</th>
                            <th style="padding: 12px; text-align: center;">Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td colspan="5" style="padding: 20px; text-align: center; color: #aaa;">
                                Carregando CRMs autorizados...
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <a href="${pageContext.request.contextPath}/dashboard-admin.jsp" class="voltar-link" style="margin-top: 30px;">
                ← Voltar ao Dashboard Administrativo
            </a>
        </c:if>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            carregarCrms();
        });

        function carregarCrms() {
            fetch('${pageContext.request.contextPath}/ListarCrmServlet')
                .then(response => response.text())
                .then(data => {
                    document.querySelector('tbody').innerHTML = data;
                })
                .catch(error => {
                    console.error('Erro ao carregar CRMs:', error);
                    document.querySelector('tbody').innerHTML = '<tr><td colspan="5" style="padding: 20px; text-align: center; color: #ff6b6b;">Erro de comunicação do servidor</td></tr>';
                });
        }

        function removerCrm(id) {
            if (confirm('Tem certeza que deseja remover esta autorização CRM?')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/RemoverCrmServlet';

                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'id';
                input.value = id;

                form.appendChild(input);
                document.body.appendChild(form);
                form.submit();
            }
        }

        // Recarregar após sucesso
        <c:if test="${not empty sucesso}">
            setTimeout(function() {
                carregarCrms();
            }, 500);
        </c:if>
    </script>
</body>
</html>
