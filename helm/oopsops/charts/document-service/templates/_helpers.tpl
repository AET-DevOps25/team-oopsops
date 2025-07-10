{{- define "document-service.name" -}}
document-service
{{- end -}}

{{- define "document-service.fullname" -}}
{{ printf "%s-%s" .Release.Name (include "document-service.name" .) | trunc 63 | trimSuffix "-" }}
{{- end -}}

{{- define "document-service.labels" -}}
app.kubernetes.io/name: {{ include "document-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}