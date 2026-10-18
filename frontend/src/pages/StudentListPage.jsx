import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listStudents } from '../api/students'

function StudentListPage() {
  // estudantes que vêm do backend
  const [students, setStudents] = useState([])
  // fica true até o backend responder
  const [loading, setLoading] = useState(true)
  // mensagem de erro se o pedido falhar
  const [error, setError] = useState('')

  // corre uma vez quando a página abre e vai buscar os estudantes
  useEffect(() => {
    async function loadStudents() {
      try {
        setStudents(await listStudents())
      } catch {
        setError('Não foi possível carregar os estudantes. Verifique se o backend está ligado.')
      } finally {
        // correu bem ou mal, o carregamento acabou
        setLoading(false)
      }
    }

    loadStudents()
  }, []) // array vazio faz com que só corra na primeira vez

  return (
    <section>
      <h2>Estudantes</h2>

      {/* leva ao formulário de criar */}
      <Link to="/students/new">Novo estudante</Link>

      {/* só aparece enquanto espera pela resposta */}
      {loading && <p>A carregar...</p>}

      {/* só aparece se o pedido falhou */}
      {error && <p role="alert">{error}</p>}

      {/* só aparece se correu bem mas ainda não há estudantes */}
      {!loading && !error && students.length === 0 && (
        <p>Ainda não existem estudantes registados.</p>
      )}

      {/* só aparece se correu bem e há estudantes */}
      {!loading && !error && students.length > 0 && (
        <table>
          <thead>
          <tr>
            <th>Número</th>
            <th>Nome</th>
            <th>Curso</th>
            <th>Ano</th>
          </tr>
          </thead>
          <tbody>
          {/* uma linha por estudante */}
          {students.map((student) => (
            <tr key={student.id}>
              <td>{student.number}</td>
              <td>
                {/* clicar no nome abre o detalhe do estudante */}
                <Link to={`/students/${student.id}`}>{student.name}</Link>
              </td>
              {/* o curso é opcional, por isso pode vir vazio */}
              <td>{student.course ?? '—'}</td>
              <td>{student.curricularYear}</td>
            </tr>
          ))}
          </tbody>
        </table>
      )}
    </section>
  )
}

export default StudentListPage
