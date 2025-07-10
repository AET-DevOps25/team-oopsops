{{- define "genai-service.name" -}}
genai-service
{{- end -}}

{{- define "genai-service.fullname" -}}
{{ printf "%s-%s" .Release.Name (include "genai-service.name" .) | trunc 63 | trimSuffix "-" }}
{{- end -}}

{{- define "genai-service.labels" -}}
app.kubernetes.io/name: {{ include "genai-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}