package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

/**
 * Filtro de autenticação que protege páginas restritas.
 * Redireciona para index.jsp se o usuário não estiver logado.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    // Páginas que NÃO precisam de login (públicas)
    private static final Set<String> PAGINAS_PUBLICAS = Set.of(
        "/index.jsp",
        "/login.jsp",
        "/login-paciente.jsp",
        "/login-medico.jsp",
        "/login-admin.jsp",
        "/cadastro.jsp",
        "/cadastro-paciente.jsp",
        "/cadastro-medico.jsp",
        "/escolha-paciente.jsp",
        "/escolha-medico.jsp",
        "/LoginServlet",
        "/CadastroServlet"
    );

    // Pastas/recursos que devem ser acessíveis sem login
    private static final Set<String> PASTAS_PUBLICAS = Set.of(
        "/css/",
        "/js/",
        "/img/",
        "/images/",
        "/favicon"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length()); // Remove context path

        // Se for a raiz "/" ou string vazia, deixa passar (vai pro index.jsp via welcome-file)
        if (path.isEmpty() || path.equals("/")) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // Se for página pública, deixa passar
        if (PAGINAS_PUBLICAS.contains(path)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // Se for recurso estático (CSS, JS, imagens), deixa passar
        for (String pasta : PASTAS_PUBLICAS) {
            if (path.startsWith(pasta)) {
                chain.doFilter(servletRequest, servletResponse);
                return;
            }
        }

        // Para tudo o resto, verificar se está logado
        HttpSession session = request.getSession(false);
        boolean logado = (session != null && session.getAttribute("usuarioEmail") != null);

        if (!logado) {
            // Redirecionar para o index.jsp
            response.sendRedirect(contextPath + "/index.jsp");
            return;
        }

        // Verificações adicionais de permissão por categoria
        Integer categoria = (Integer) session.getAttribute("usuarioCategoria");

        // Páginas de admin: só categoria 0
        if (path.startsWith("/dashboard-admin") || path.startsWith("/admin-") ||
            path.equals("/BackupDatabaseServlet") || path.equals("/RestaurarDatabaseServlet") ||
            path.equals("/CadastrarCrmServlet") || path.equals("/ListarCrmServlet") ||
            path.equals("/RemoverCrmServlet") || path.equals("/ListarUsuariosAdminServlet") ||
            path.equals("/GerarGraficoAdminServlet")) {
            if (categoria == null || categoria != 0) {
                response.sendRedirect(contextPath + "/index.jsp");
                return;
            }
        }

        // Páginas de médico: só categoria 2
        if (path.startsWith("/dashboard-medico") || path.equals("/minha-agenda-medico.jsp") ||
            path.equals("/tipos-consulta.jsp") || path.equals("/disponibilidade-profissional.jsp") ||
            path.equals("/DefineDisponibilidadeServlet") || path.equals("/GerarConsultasDisponiveisServlet") ||
            path.equals("/ListarDisponibilidadesServlet") || path.equals("/RemoverDisponibilidadeServlet")) {
            if (categoria == null || categoria != 2) {
                response.sendRedirect(contextPath + "/index.jsp");
                return;
            }
        }

        // Páginas de paciente: só categoria 1
        if (path.startsWith("/dashboard-paciente") || path.equals("/agendar-atendimento.jsp") ||
            path.equals("/minhas-consultas.jsp") || path.equals("/custos-totais.jsp")) {
            if (categoria == null || categoria != 1) {
                response.sendRedirect(contextPath + "/index.jsp");
                return;
            }
        }

        // Se passou tudo, deixa acessar
        chain.doFilter(servletRequest, servletResponse);
    }
}
