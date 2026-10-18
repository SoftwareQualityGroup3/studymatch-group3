import { Link, useParams } from 'react-router-dom'
import useStudent from '../hooks/useStudent'

// códigos das issues que vão preencher estes espaços mais tarde
const RESERVED_SECTIONS = ['S2-06', 'S2-09', 'S2-11']

function StudentDetailPage() {
  // lê o id que vem no endereço, tipo /students/4
  const { id } = useParams()

  // vai buscar o estudante ao backend
  const { student, loading, error } = useStudent(id)

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
            <dd>{student.number}</dd>
            <dt>Contacto institucional</dt>
            <dd>{student.email ?? '—'}</dd>
            <dt>Curso</dt>
            <dd>{student.course ?? '—'}</dd>
            <dt>Ano curricular</dt>
            <dd>{student.curricularYear}</dd>
          </dl>

          {/* espaços vazios onde as próximas issues vão pôr o seu conteúdo */}
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
