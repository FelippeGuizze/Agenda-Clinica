<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Definir Disponibilidade - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="form-container">
        <h1 style="font-size: 2em; margin-bottom: 30px;">Definir Disponibilidade</h1>

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

        <c:if test="${sessionScope.usuarioCategoria != 2}">
            <div class="erro">
                <strong>❌ Acesso Restrito:</strong> Apenas profissionais podem definir disponibilidade!
            </div>
            <a href="${pageContext.request.contextPath}/dashboard-medico.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria == 2}">
            <div style="margin-bottom: 30px; padding: 15px; background: rgba(0, 212, 255, 0.1); border-radius: 5px; border-left: 4px solid #00d4ff;">
                <strong>ℹ️ Informação:</strong> Configure seus horários de disponibilidade para que os pacientes possam agendar atendimentos com você.
            </div>

            <form method="POST" action="${pageContext.request.contextPath}/DefineDisponibilidadeServlet">
                <div class="form-group">
                    <label for="dia">Dia da Semana:</label>
                    <select id="dia" name="dia" required>
                        <option value="">-- Selecione --</option>
                        <option value="SEGUNDA">Segunda-feira</option>
                        <option value="TERÇA">Terça-feira</option>
                        <option value="QUARTA">Quarta-feira</option>
                        <option value="QUINTA">Quinta-feira</option>
                        <option value="SEXTA">Sexta-feira</option>
                        <option value="SABADO">Sábado</option>
                        <option value="DOMINGO">Domingo</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="horario">Horário:</label>
                    <input type="time" id="horario" name="horario" required>
                </div>

                <div class="form-group">
                    <label for="ativo">
                        <input type="checkbox" id="ativo" name="ativo" value="true" checked>
                        Disponível neste horário
                    </label>
                </div>

                <button type="submit" class="btn-submit">Adicionar Disponibilidade</button>
            </form>

            <hr style="border: 1px solid rgba(255, 255, 255, 0.2); margin: 30px 0;">

            <h2 style="color: #00d4ff; margin-bottom: 20px;">Suas Disponibilidades</h2>
            
            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 2px solid #00d4ff;">
                            <th style="padding: 10px; text-align: left;">Dia</th>
                            <th style="padding: 10px; text-align: left;">Horário</th>
                            <th style="padding: 10px; text-align: left;">Status</th>
                            <th style="padding: 10px; text-align: center;">Ação</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Será preenchida dinamicamente -->
                        <tr>
                            <td colspan="4" style="padding: 10px; text-align: center; color: #aaa;">
                                Carregando disponibilidades...
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <a href="${pageContext.request.contextPath}/dashboard-medico.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>
    </div>

    <script>
        // Carregar disponibilidades ao carregar a página
        document.addEventListener('DOMContentLoaded', function() {
            carregarDisponibilidades();
        });

        // Função para carregar disponibilidades via AJAX
        function carregarDisponibilidades() {
            fetch('${pageContext.request.contextPath}/ListarDisponibilidadesServlet')
                .then(response => response.text())
                .then(data => {
                    document.querySelector('tbody').innerHTML = data;
                })
                .catch(error => {
                    console.error('Erro ao carregar disponibilidades:', error);
                    document.querySelector('tbody').innerHTML = '<tr><td colspan="4" style="padding: 10px; text-align: center; color: #ff6b6b;">Erro ao carregar dados</td></tr>';
                });
        }

        // Função para remover disponibilidade
        function removerDisponibilidade(id) {
            if (confirm('Tem certeza que deseja remover esta disponibilidade?')) {
                // Criar formulário dinâmico para POST
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/RemoverDisponibilidadeServlet';

                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'id';
                input.value = id;

                form.appendChild(input);
                document.body.appendChild(form);
                form.submit();
            }
        }

        // Recarregar após adicionar nova disponibilidade
        <c:if test="${not empty sucesso}">
            setTimeout(function() {
                carregarDisponibilidades();
            }, 1000);
        </c:if>
    </script>
</body>
</html>
