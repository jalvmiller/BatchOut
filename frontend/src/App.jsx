import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./components/layout/Layout";
import Dashboard from "./pages/Dashboard";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Configuracoes from "./pages/Configuracoes";
import QuestaoDetalhes from "./components/questao/QuestaoDetalhes";
import { AuthProvider } from "./contexts/AuthContext";
import PrivateRoute from "./components/layout/PrivateRoute";

function App() {
	/* 
	BrowserRouter é um componente que cria o ambiente de navegação para a aplicação.
	Ele diz para o navegador qual tela carregar para cada endereço.. 
	ex: Se for "/login" carrega o Login, se for "/register" carrega o Register
    
	Routes funciona como um painel de controle que verifica qual é a URL da barra
	de endereço, e procura de cima pra baixo qual é a rota correspondente.
    
	Rota Pública = qualquer pessoa acessa e, quando a URL corresponde, a tela vai
	ser limpa e vai renderizar só o card de Login.. não vai usar menus ou o header
    
	Rota Privada = só usuários autenticados acessam e,
	se a URL for a raiz (/) então o react deve carregar o componente
	Layout. O Layout por fora é "invisível", ele só existe para abrigar as rotas que precisam 
	dele. É aqui dentro do Layout que fica o menu e o header. Então, ao entrar na raiz, 
	o usuário vê o menu e o header e, no meio deles, aparece o Dashboard.
	*/
	return (
		<AuthProvider>
			{/* O AuthProvider vai envolver toda a aplicação
				se o usuário estiver logado, ele poderá acessar as rotas privadas
				se não estiver logado, será redirecionado para a rota pública /login
				Quando o usuário fizer login, a função login() dentro do AuthContext será chamada
				que vai atualizar o estado do usuário e do token, como o estado é global, o 
				React percebe isso e redesenha a tela com o Dashboard logado 
			*/}
			<BrowserRouter>
				<Routes>
					{/* Rotas Públicas */}
					<Route path="/login" element={<Login />} />
					<Route path="/register" element={<Register />} />

					{/* Rotas Privadas (protegidas pelo PrivateRoute e dentro do Layout) */}
					<Route
						path="/questoes/:id"
						element={
							<PrivateRoute>
								<Layout>
									<QuestaoDetalhes />
								</Layout>
							</PrivateRoute>
						}
					/>
					<Route
						path="/configuracoes"
						element={
							<PrivateRoute>
								<Layout>
									<Configuracoes />
								</Layout>
							</PrivateRoute>
						}
					/>
					<Route
						path="/"
						element={
							<PrivateRoute>
								<Layout>
									<Dashboard />
								</Layout>
							</PrivateRoute>
						}
					/>
				</Routes>
			</BrowserRouter >
		</AuthProvider>
	);
}

export default App;