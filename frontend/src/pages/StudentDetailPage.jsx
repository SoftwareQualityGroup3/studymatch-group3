import { Link, useParams } from 'react-router-dom'
import useStudent from '../hooks/useStudent'
import AcademicHistorySection from '../components/AcademicHistorySection'
import ProfileSection from '../components/ProfileSection'
import SelfAssessmentSection from '../components/SelfAssessmentSection'

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

          {/* S2-06: histórico académico, avisa o perfil quando há um resultado novo */}
          <AcademicHistorySection studentId={student.id} />
          {/* S2-09: perfil calculado, recarrega sozinho quando há resultados ou autoavaliação novos */}
          <ProfileSection studentId={student.id} />
          {/* S2-11: o estudante preenche a autoavaliação inicial */}
          <SelfAssessmentSection studentId={student.id} />
        </>
      )}
    </section>
  )
}

export default StudentDetailPage