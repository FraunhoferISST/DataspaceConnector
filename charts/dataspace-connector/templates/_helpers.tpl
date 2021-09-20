{{/*
Expand the name of the chart.
*/}}
{{- define "dataspace-connector.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "dataspace-connector.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "dataspace-connector.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "dataspace-connector.labels" -}}
helm.sh/chart: {{ include "dataspace-connector.chart" . }}
{{ include "dataspace-connector.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "dataspace-connector.selectorLabels" -}}
app.kubernetes.io/name: {{ include "dataspace-connector.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "dataspace-connector.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "dataspace-connector.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Set environment variables
*/}}
{{- define "dataspace-connector.env-variables" -}}
{{- $name := include "dataspace-connector.fullname" .}}
{{- range $key, $value := .Values.env.config }}
- name: {{ $key }}
  valueFrom:
    configMapKeyRef:
      name: {{ $name }}
      key: {{ $key }}
{{- end }}
{{- if .Values.postgresql.enabled }}
- name: SPRING_DATASOURCE_PLATFORM
  valueFrom:
    configMapKeyRef:
      name: {{ $name }}
      key: SPRING_DATASOURCE_PLATFORM
- name: SPRING_DATASOURCE_URL
  valueFrom:
    configMapKeyRef:
      name: {{ $name }}
      key: SPRING_DATASOURCE_URL
- name: SPRING_DATASOURCE_DRIVERCLASSNAME
  valueFrom:
    configMapKeyRef:
      name: {{ $name }}
      key: SPRING_DATASOURCE_DRIVERCLASSNAME
- name: SPRING_JPA_DATABASEPLATFORM
  valueFrom:
    configMapKeyRef:
      name: {{ $name }}
      key: SPRING_JPA_DATABASEPLATFORM
{{- end }}
{{- range $key, $value := .Values.env.secrets }}
- name: {{ $key }}
  valueFrom:
    secretKeyRef:
      name: {{ $name }}
      key: {{ $key }}
{{- end }}
{{- if .Values.postgresql }}
- name: SPRING_DATASOURCE_USERNAME
  valueFrom:
    secretKeyRef:
      name: {{ $name }}
      key: SPRING_DATASOURCE_USERNAME
- name: SPRING_DATASOURCE_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ $name }}
      key: SPRING_DATASOURCE_PASSWORD
{{- end }}
{{- end }}

{{/*
Generate ssl certificates
*/}}
{{- define "dataspace-connector.gen-certs" -}}
{{- $altNames := list ( printf "%s.%s" (include "dataspace-connector.name" .) .Release.Namespace ) ( printf "%s.%s.svc" (include "dataspace-connector.name" .) .Release.Namespace ) -}}
{{- $ca := genCA "dataspace-connector-ca" 1 -}}
{{- $cert := genSignedCert ( include "dataspace-connector.name" . ) nil $altNames 1 $ca -}}
tls.crt: {{ $cert.Cert | b64enc }}
tls.key: {{ $cert.Key | b64enc }}
{{- end -}}
