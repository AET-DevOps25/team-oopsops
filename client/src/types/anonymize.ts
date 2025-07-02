import { UUID } from "crypto";

export interface AnonymizedDocument {
    id: UUID,
    documentId: UUID
    userId : UUID
    fileName: string,
    status: string
}
