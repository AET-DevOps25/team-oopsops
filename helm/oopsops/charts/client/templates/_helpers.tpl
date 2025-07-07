{{- define "client.name" -}}
client
{{- end -}}

{{- define "client.fullname" -}}
{{ printf "%s-%s" .Release.Name (include "client.name" .) | trunc 63 | trimSuffix "-" }}
{{- end -}}

{{- define "client.labels" -}}
app.kubernetes.io/name: {{ include "client.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}
