{{- define "oopsops.ingress.host" -}}
{{- if .Values.ingress.host }}
  {{- .Values.ingress.host }}
{{- else if eq .Release.Namespace "oops-ops" -}}
  redactiq.student.k8s.aet.cit.tum.de
{{- else -}}
  test.redactiq.student.k8s.aet.cit.tum.de
{{- end -}}
{{- end }}

{{- define "oopsops-app.fullname" -}}
{{ printf "%s-%s" .Release.Name .Chart.Name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "oopsops-app.ingressHost" -}}
{{- .Values.ingress.host }}
{{- end }}