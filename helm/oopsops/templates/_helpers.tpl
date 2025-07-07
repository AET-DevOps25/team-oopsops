{{- define "oopsops.ingress.host" -}}
{{- if .Values.ingress.host }}
  {{- .Values.ingress.host }}
{{- else if eq .Release.Namespace "oops-ops" -}}
  redactiq.student.k8s.aet.cit.tum.de
{{- else -}}
  test.redactiq.student.k8s.aet.cit.tum.de
{{- end -}}
{{- end }}

{{- define "oopsops-app.name" -}}
{{ default .Chart.Name .Values.nameOverride }}
{{- end -}}

{{- define "oopsops-app.fullname" -}}
{{ printf "%s-%s" (include "oopsops-app.name" .) .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end -}}