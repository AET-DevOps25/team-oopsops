{{- define "postgresql.name" -}}
postgresql
{{- end -}}

{{- define "postgresql.fullname" -}}
{{ include "postgresql.name" . }}-{{ .Release.Name }}
{{- end -}}