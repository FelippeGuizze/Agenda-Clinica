<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Relatórios - ADMIN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <!-- INJETANDO CHART.JS VIA CDN PÚBLICA DE ALTA VELOCIDADE -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <div class="table-container" style="max-width: 1200px;">
        <h1 style="font-size: 2em; margin-bottom: 30px;">Faturamento Macroeconômico da Clínica</h1>

        <c:if test="${sessionScope.usuarioCategoria != 0}">
            <div class="erro">
                <strong>❌ Acesso Restrito:</strong> Área exclusíva para Administradores.
            </div>
            <a href="${pageContext.request.contextPath}/index.jsp" class="voltar-link">Voltar ao Inicio</a>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria == 0}">
            
            <div class="form-group" style="margin-bottom: 30px; max-width: 300px; margin: 0 auto 30px auto;">
                <label for="anoSelect" style="text-align:center;">Selecione o Exercício Fiscal (Ano):</label>
                <select id="anoSelect" onchange="MudarAno()" style="font-size: 1.2em; text-align:center;">
                    <!-- Sera Preenchido Pelo JS Dinamicamente -->
                    <option value="" disabled selected>Carregando...</option>
                </select>
            </div>
            
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px;">
                <div style="background: rgba(255, 202, 40, 0.2); border-left: 4px solid #ffca28; padding: 15px; border-radius: 5px;">
                    <h3 style="color: #ffca28; margin: 0 0 5px 0;">Faturamento Bruto Total</h3>
                    <h2 id="displayTotalBruto" style="margin: 0; font-size: 1.8em;">R$ 0,00</h2>
                </div>
                <div style="background: rgba(0, 212, 255, 0.2); border-left: 4px solid #00d4ff; padding: 15px; border-radius: 5px;">
                    <h3 style="color: #00d4ff; margin: 0 0 5px 0;">Faturamento Líquido (Com 10% da Plataforma)</h3>
                    <h2 id="displayTotalLiquido" style="margin: 0; font-size: 1.8em;">R$ 0,00</h2>
                </div>
            </div>

            <!-- CONTAINER PARA O CANVAS DO CHART.JS -->
            <div style="background: rgba(0,0,0,0.3); padding: 20px; border-radius: 10px;">
                <canvas id="faturamentoChart" height="120"></canvas>
            </div>

            <a href="${pageContext.request.contextPath}/dashboard-admin.jsp" class="voltar-link" style="margin-top:40px;">Voltar ao Dashboard Administrativo</a>
        </c:if>
    </div>

    <script>
        let myChart = null;
        let jaPopulatedAnos = false;

        document.addEventListener('DOMContentLoaded', function() {
            carregarGrafico(''); // Vazio p/ pegar ano mais recente automatico
        });

        function MudarAno() {
            carregarGrafico(document.getElementById('anoSelect').value);
        }

        function carregarGrafico(anoDesejado) {
            
            fetch('${pageContext.request.contextPath}/GerarGraficoAdminServlet?ano=' + anoDesejado)
                .then(response => response.json())
                .then(data => {
                    
                    // Se for a primeira vez carregando, preenche dinamicamente todas as options baseado na DB
                    if (!jaPopulatedAnos) {
                        const selectAno = document.getElementById('anoSelect');
                        selectAno.innerHTML = ''; // Limpa o "Carregando"
                        
                        let selectValue = anoDesejado;
                        if(anoDesejado === '') selectValue = data.anosDisponiveis[0];

                        data.anosDisponiveis.forEach(anoStr => {
                            const opt = document.createElement('option');
                            opt.value = anoStr;
                            opt.innerText = anoStr;
                            if (anoStr.toString() === selectValue.toString()) {
                                opt.selected = true;
                            }
                            selectAno.appendChild(opt);
                        });
                        jaPopulatedAnos = true;
                    }
                    
                    const renderingAno = document.getElementById('anoSelect').value;
                    
                    // Update text blocks above
                    const frmBRL = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
                    document.getElementById('displayTotalBruto').innerText = frmBRL.format(data.totalBruto);
                    document.getElementById('displayTotalLiquido').innerText = frmBRL.format(data.totalLiquido);

                    renderizarGrafico(data.meses, data.valoresLiquidos, data.valoresBrutos, renderingAno);
                })
                .catch(error => {
                    console.error('Erro ao buscar relatórios:', error);
                    alert("Erro ao buscar dados do gráfico!");
                });
        }

        function renderizarGrafico(meses, valoresLiquidos, valoresBrutos, ano) {
            const ctx = document.getElementById('faturamentoChart').getContext('2d');
            
            if (myChart != null) {
                myChart.destroy();
            }

            myChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: meses,
                    datasets: [
                        {
                            label: 'Faturamento Bruto (Sem Taxa)',
                            data: valoresBrutos,
                            backgroundColor: 'rgba(255, 202, 40, 0.6)',
                            borderColor: 'rgba(255, 202, 40, 1)',
                            borderWidth: 2,
                            borderRadius: 5,
                            hoverBackgroundColor: 'rgba(255, 202, 40, 0.8)',
                        },
                        {
                            label: 'Faturamento Total Líquido (Com 10% Aplicado)',
                            data: valoresLiquidos,
                            backgroundColor: 'rgba(0, 212, 255, 0.6)',
                            borderColor: 'rgba(0, 212, 255, 1)',
                            borderWidth: 2,
                            borderRadius: 5,
                            hoverBackgroundColor: 'rgba(0, 255, 136, 0.8)',
                        }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { labels: { color: 'white', font: { size: 14 } } },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    let label = context.dataset.label || '';
                                    if (label) { label += ': '; }
                                    if (context.parsed.y !== null) {
                                        label += new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(context.parsed.y);
                                    }
                                    return label;
                                }
                            }
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: { color: '#ccc' },
                            grid: { color: 'rgba(255,255,255,0.1)' }
                        },
                        x: {
                            ticks: { color: '#ccc' },
                            grid: { display: false }
                        }
                    }
                }
            });
        }
    </script>
</body>
</html>
