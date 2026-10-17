import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getStudent } from '../api/students'
import { STUDENT_STATUSES } from '../components/StudentForm'

// códigos das issues que vão ocupar estes espaços mais tarde
const RESERVED_SECTIONS = ['S2-06', 'S2-09', 'S2-11']

// passa ACTIVE para Ativo e assim por diante
// se não encontrar mostra o valor tal como veio
function statusLabel(status) {
  return STUDENT_STATUSES.find((item) => item.value === status)?.label ?? status
}

function StudentDetailPage() {
  // lê o id que vem no endereço tipo /students/4
  const { id } = useParams()

  // estudante que vem do servidor
  const [student, setStudent] = useState(null)
  // fica true até o servidor responder
  const [loading, setLoading] = useState(true)
  // mensagem de erro se não conseguir carregar
  const [error, setError] = useState('')

  // corre quando a página abre e vai buscar o estudante
  useEffect(() => {
    async function loadStudent() {
      try {
        setStudent(await getStudent(id))
      } catch (e) {
        // 404 quer dizer que não existe qualquer outro erro é do servidor
        setError(
          e.status === 404
            ? 'Estudante não encontrado.'
            : 'Não foi possível carregar o estudante. Verifique se o backend está ligado.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadStudent()
  }, [id]) // corre outra vez se o id mudar

  return (
    <section>
      <Link to="/students">Voltar à lista</Link>

      {/* só aparece enquanto espera pela resposta */}
      {loading && <p>A carregar...</p>}

      {/* só aparece se não conseguiu carregar o estudante */}
      {error && <p role="alert">{error}</p>}

      {/* só mostra os dados depois de o estudante chegar */}
      {student && (
        <>
          <h2>{student.name}</h2>
          <Link to={`/students/${student.id}/edit`}>Editar</Link>

          <dl>
            <dt>Número de estudante</dt>
            <dd>{student.studentNumber}</dd>
            <dt>Contacto institucional</dt>
            <dd>{student.email}</dd>
            <dt>Curso</dt>
            <dd>{student.course}</dd>
            <dt>Plano de estudos</dt>
            <dd>{student.studyPlan}</dd>
            <dt>Ano letivo de ingresso</dt>
            <dd>{student.enrollmentYear}</dd>
            <dt>Ano curricular</dt>
            <dd>{student.curricularYear}</dd>
            <dt>Estado</dt>
            <dd>{statusLabel(student.status)}</dd>
          </dl>

          {/* espaços vazios para as próximas issues irem preenchendo */}
          {RESERVED_SECTIONS.map((code) => (
            <section key={code} data-testid={`reserved-${code}`}>
              <p>Espaço reservado para {code}.</p>
            </section>
          ))}
        </>
      )}
    </section>
  )
}

export default StudentDetailPage