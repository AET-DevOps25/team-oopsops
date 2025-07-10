{{- define "authentication-service.name" -}}
authentication-service
{{- end -}}

{{- define "authentication-service.fullname" -}}
{{ printf "%s-%s" .Release.Name (include "authentication-service.name" .) | trunc 63 | trimSuffix "-" }}
{{- end -}}

{{- define "authentication-service.labels" -}}
app.kubernetes.io/name: {{ include "authentication-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}