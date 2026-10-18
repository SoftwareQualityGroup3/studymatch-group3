import { useEffect, useState } from 'react'
import {
  createAcademicRecord,
  listAcademicRecords,
  listCourseUnits,
} from '../api/academicRecords'

function AcademicHistorySection({ studentId, onRecordCreated }) {
  const [records, setRecords] = useState([])
  const [courseUnits, setCourseUnits] = useState([])

  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState('')
  const [saving, setSaving] = useState(false)

  const [formError, setFormError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})

  const [form, setForm] = useState({
    courseUnitCode: '',
    grade: '',
    academicYear: '',
    semester: '1',
  })

  // Carrega as UCs e o histórico do estudante.
  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true)
        setLoadError('')

        const [recordsData, courseUnitsData] =
          await Promise.all([
            listAcademicRecords(studentId),
            listCourseUnits(),
          ])

        setRecords(recordsData)
        setCourseUnits(courseUnitsData)
      } catch (error) {
        setLoadError(error.message)
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [studentId])

  function handleChange(event) {
    const { name, value } = event.target

    setForm((current) => ({
      ...current,
      [name]: value,
    }))
  }

  async function handleSubmit(event) {
    event.preventDefault()

    setSaving(true)
    setFormError('')
    setFieldErrors({})

    try {
      await createAcademicRecord(studentId, {
        courseUnitCode: form.courseUnitCode,
        grade: Number(form.grade),
        academicYear: form.academicYear,
        semester: Number(form.semester),
      })

      // Volta a carregar o histórico para mostrar o novo resultado.
      const updatedRecords =
        await listAcademicRecords(studentId)

      setRecords(updatedRecords)

      setForm({
        courseUnitCode: '',
        grade: '',
        academicYear: '',
        semester: '1',
      })

      // Avisa a futura secção S2-09 de que o perfil deve ser atualizado.
      window.dispatchEvent(
        new CustomEvent('student-profile-refresh', {
          detail: { studentId },
        })
      )

      // Permite também usar uma função passada pelo componente pai.
      onRecordCreated?.()
    } catch (error) {
      const errors = error.fieldErrors ?? {}

      setFieldErrors(errors)

      // Só mostra erro geral quando não existe erro associado
      // a um campo específico do formulário.
      if (Object.keys(errors).length === 0) {
        setFormError(error.message)
      } else {
        setFormError('')
      }
    } finally {
      setSaving(false)
    }
  }

  return (
    <section>
      <h3>Histórico académico</h3>

      {loading && <p>A carregar histórico...</p>}

      {loadError && (
        <p role="alert">{loadError}</p>
      )}

      {!loading && !loadError && (
        <>
          {records.length === 0 ? (
            <p>Sem resultados académicos.</p>
          ) : (
            <table>
              <thead>
              <tr>
                <th>UC</th>
                <th>Nota</th>
                <th>Ano letivo</th>
                <th>Semestre</th>
              </tr>
              </thead>

              <tbody>
              {records.map((record) => (
                <tr key={record.id}>
                  <td>{record.courseUnitName}</td>
                  <td>{record.grade}</td>
                  <td>{record.academicYear}</td>
                  <td>{record.semester}</td>
                </tr>
              ))}
              </tbody>
            </table>
          )}

          <h4>Registar novo resultado</h4>

          <form onSubmit={handleSubmit}>
            <div>
              <label htmlFor="courseUnitCode">
                Unidade curricular
              </label>

              <select
                id="courseUnitCode"
                name="courseUnitCode"
                value={form.courseUnitCode}
                onChange={handleChange}
                required
              >
                <option value="">
                  Selecione uma UC
                </option>

                {courseUnits.map((courseUnit) => (
                  <option
                    key={courseUnit.code}
                    value={courseUnit.code}
                  >
                    {courseUnit.name}
                  </option>
                ))}
              </select>

              {fieldErrors.courseUnitCode && (
                <p role="alert">
                  {fieldErrors.courseUnitCode}
                </p>
              )}
            </div>

            <div>
              <label htmlFor="grade">Nota</label>

              <input
                id="grade"
                name="grade"
                type="number"
                min="0"
                max="20"
                step="0.01"
                value={form.grade}
                onChange={handleChange}
                required
              />

              {fieldErrors.grade && (
                <p role="alert">
                  {fieldErrors.grade}
                </p>
              )}
            </div>

            <div>
              <label htmlFor="academicYear">
                Ano letivo
              </label>

              <input
                id="academicYear"
                name="academicYear"
                type="text"
                placeholder="2026/27"
                value={form.academicYear}
                onChange={handleChange}
                required
              />

              {fieldErrors.academicYear && (
                <p role="alert">
                  {fieldErrors.academicYear}
                </p>
              )}
            </div>

            <div>
              <label htmlFor="semester">
                Semestre
              </label>

              <select
                id="semester"
                name="semester"
                value={form.semester}
                onChange={handleChange}
              >
                <option value="1">1.º semestre</option>
                <option value="2">2.º semestre</option>
              </select>

              {fieldErrors.semester && (
                <p role="alert">
                  {fieldErrors.semester}
                </p>
              )}
            </div>

            {formError && (
              <p role="alert">{formError}</p>
            )}

            <button
              type="submit"
              disabled={saving}
            >
              {saving
                ? 'A guardar...'
                : 'Registar resultado'}
            </button>
          </form>
        </>
      )}
    </section>
  )
}

export default AcademicHistorySection
