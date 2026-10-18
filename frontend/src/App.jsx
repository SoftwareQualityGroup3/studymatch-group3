import { Link, Navigate, Route, Routes } from 'react-router-dom'
import HealthPage from './pages/HealthPage'
import StudentCreatePage from './pages/StudentCreatePage'
import StudentDetailPage from './pages/StudentDetailPage'
import StudentEditPage from './pages/StudentEditPage'
import StudentListPage from './pages/StudentListPage'

// aqui liga-se cada endereço a uma página
// quando houver mais páginas é só acrescentar uma rota
function App() {
  return (
    <main>
      <h1>StudyMatch</h1>

      {/* links que aparecem sempre no topo */}
      <nav>
        <Link to="/students">Estudantes</Link> <Link to="/health">Estado do sistema</Link>
      </nav>

      <Routes>
        {/* a raiz manda logo para a lista */}
        <Route path="/" element={<Navigate to="/students" replace />} />
        <Route path="/students" element={<StudentListPage />} />
        {/* o /new tem de vir antes do /:id, senão o "new" era tratado como um id */}
        <Route path="/students/new" element={<StudentCreatePage />} />
        {/* o :id é a parte que muda, tipo /students/4 */}
        <Route path="/students/:id" element={<StudentDetailPage />} />
        <Route path="/students/:id/edit" element={<StudentEditPage />} />
        <Route path="/health" element={<HealthPage />} />
      </Routes>
    </main>
  )
}

export default App
