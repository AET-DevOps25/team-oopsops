{{- define "anonymization-service.name" -}}
anonymization-service
{{- end -}}

{{- define "anonymization-service.fullname" -}}
{{ printf "%s-%s" .Release.Name (include "anonymization-service.name" .) | trunc 63 | trimSuffix "-" }}
{{- end -}}

{{- define "anonymization-service.labels" -}}
app.kubernetes.io/name: {{ include "anonymization-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}